package com.rkgen.jsontorestkitgenerator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.List;

import net.sf.ezmorph.bean.MorphDynaBean;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.rkgen.jsontorestkitgenerator.exception.RKCodeGenException;

/**
 * Main class in RKGen
 * @author mhc
 *
 */
public class RKCodeGen {

    private static Logger logger = Logger.getLogger("RKCodeGen");

    private RKObjectBuilder objectBuilder = null;
    
    private RKFilesGenerator filesGenerator = null;
    
    private String rootObjectName = null;
    
    
    /**
     * Constructor of RKCodeGen, initialize all the required objects required to generate the objective-c files
     * 
     * @param templatePath
     *          The velocity template path
     * @param attributePropFile
     *          Absolute path to the property to property type mapping properties file
     * @param restrictedKeywordsFile
     *          Absolute path to the restricted keyword file
     * @param outputPath
     *          The output path
     * @param rootObjectName
     *          Root object name
     * @param classPrefix
     *          Prefix of the generated class
     * @throws RKCodeGenException
     *          If any of the parameters is not valid or not pointing to the right files / directory
     */
    public RKCodeGen(String templatePath, String attributePropFile, String restrictedKeywordsFile, String outputPath, String rootObjectName, String classPrefix) throws RKCodeGenException {
        logger.info("Initializing RKCodeGen...");
        
        if (org.apache.commons.lang.StringUtils.isEmpty(rootObjectName)){
            throw new RKCodeGenException("Root object name cannot be null or empty");
        }
        
        if (!org.apache.commons.lang.StringUtils.isEmpty(classPrefix)){
            logger.info("Using class prefix: " + classPrefix);
        }
        
        logger.info("Using root object name: " + rootObjectName);
        this.rootObjectName = rootObjectName;
        
        RKObjectNamesProvider namesProvider = null;
        
        try {
            namesProvider = new RKObjectNamesProvider(attributePropFile, restrictedKeywordsFile, classPrefix);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RKCodeGenException(e.getMessage());
        }
        
        objectBuilder = new RKObjectBuilder(namesProvider);
        
        logger.info("Checking outputPath...");
        
        File outputPathFile = new File(outputPath);
        
        if (!outputPathFile.isDirectory()){
            throw new RKCodeGenException("Output path must be a directory...");
        }
        
        if (!outputPath.endsWith("/")){
            outputPath = outputPath + "/";
        }
        
        logger.info("Initializing Velocity...");

        TemplatesProvider templatesProvider = new TemplatesProvider(templatePath);
        
        filesGenerator = new RKFilesGenerator(templatesProvider, outputPath, namesProvider);
       
        

    }

    /**
     * Invoke this method to generate Objective-C code from the {@link InputStream}
     * @param stream
     * @throws RKCodeGenException
     */
    public void generateCode(InputStream stream) throws RKCodeGenException {
        
        MorphDynaBean bean = convertInputStreamToMorphDynaBean(stream);
        
        List<RKObject> rkObjects = this.objectBuilder.generateRKObjects(bean, rootObjectName);
        
        if (rkObjects == null || rkObjects.isEmpty()){
            throw new RKCodeGenException("No JSON objects found!");
        }
        
        filesGenerator.generateFiles(rkObjects, this.rootObjectName);
        
    }
    
    
    /**
     * Converts the JSON {@link InputStream} to a {@link MorphDynaBean}
     * 
     * @param inputStream
     *      InputStream containing the JSON to generate
     * @return
     *      The {@link MorphDynaBean} created according to the body of JSON
     * @throws RKCodeGenException
     *      For any error reading / parsing the JSON file
     */
    public MorphDynaBean convertInputStreamToMorphDynaBean(InputStream inputStream) throws RKCodeGenException{

        MorphDynaBean bean = null;
        
        String jsonString = null;

        //Convert stream to string
        try {
            StringWriter stringWriter = new StringWriter();
            
            IOUtils.copy(inputStream, stringWriter);
            
            jsonString = stringWriter.toString();
            logger.info("Source for code generation: \n" + jsonString);
        } catch (IOException e1) {
            e1.printStackTrace();
            throw new RKCodeGenException("Error reading input");
        }

        //Read json
        try {
            JSONObject obj = JSONObject.fromObject(jsonString);

            Object ooo = JSONObject.toBean(obj);

            bean = (MorphDynaBean) ooo;


        } catch (JSONException e) {
            
            e.printStackTrace();
            throw new RKCodeGenException("Problem parsing JSON...");
            
        }

        return bean;
    }
    
}
