package com.rkgen.jsontorestkitgenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import net.sf.ezmorph.bean.MorphDynaBean;
import net.sf.ezmorph.bean.MorphDynaClass;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.util.StringUtils;

import com.rkgen.jsontorestkitgenerator.exception.RKCodeGenException;

/**
 * Main class in RKGen
 * @author mhc
 *
 */
public class RKCodeGen {

    private static Logger logger = Logger.getLogger("RKCodeGen");

    private static final String OBJECT_HEADER_TEMPLATE_NAME = "headertemplate.vm";

    private static final String OBJECT_IMPLEMENTATION_TEMPLATE_NAME = "implementationtemplate.vm";
    
    private static final String MAPPER_HEADER_TEMPLATE_NAME = "mapperHeaderTemplate.vm";

    private static final String DOT_H = ".h";

    private static final String DOT_M = ".m";
    
    private static final String MAPPER_FILE_SUFFIX = "Mapper";

    private Properties prop = new Properties();

    private ArrayList<RKObject> rkObjects = new ArrayList<RKObject>();

    private String templatePath = null;

    private List<String> restrictedKeywords = null;
    
    private String outputPath = null;

    private String rootObjectName = null;

    private String classPrefix = null;
    
    public RKCodeGen(String templatePath, String attributePropFile, String restrictedKeywordsFile, String outputPath, String rootObjectName, String classPrefix) throws RKCodeGenException {
        logger.info("Initializing RKCodeGen...");
        
        if (org.apache.commons.lang.StringUtils.isEmpty(rootObjectName)){
            
            throw new RKCodeGenException("Root object name cannot be null or empty");
            
        }
        
        if (!org.apache.commons.lang.StringUtils.isEmpty(classPrefix)){
            logger.info("Using class prefix: " + classPrefix);
            this.classPrefix = classPrefix;
        }
        
        this.rootObjectName = rootObjectName;
        
        logger.info("Checking outputPath...");
        
        File outputPathFile = new File(outputPath);
        
        if (!outputPathFile.isDirectory()){
            throw new RKCodeGenException("Output path must be a directory...");
        }
        
        if (!outputPath.endsWith("/")){
            outputPath = outputPath + "/";
        }
        
        this.outputPath = outputPath;
        
        logger.info("Initializing Velocity...");

        Velocity.setProperty(RuntimeConstants.RESOURCE_LOADER, "file");
        Velocity.setProperty("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.FileResourceLoader");
        this.templatePath = templatePath;
        logger.info("Setting Velocity Template path to: " + templatePath);
        Velocity.setProperty("file.resource.loader.path", this.templatePath);
        Velocity.init();

        Template objectHeaderTemplate = null;

        Template objectImplementationTemplate = null;
        
        Template mapperHeaderTemplate = null;
        
        objectHeaderTemplate = getTemplate(OBJECT_HEADER_TEMPLATE_NAME);

        if (objectHeaderTemplate == null) {
            throw new RKCodeGenException("Cannot initialize template for generating object header");
        }

        objectImplementationTemplate = getTemplate(OBJECT_IMPLEMENTATION_TEMPLATE_NAME);

        if (objectImplementationTemplate == null) {
            throw new RKCodeGenException("Cannot initialize template for generating object implementation");
        }
        
        mapperHeaderTemplate = getTemplate(MAPPER_HEADER_TEMPLATE_NAME);

        if (mapperHeaderTemplate == null) {
            throw new RKCodeGenException("Cannot initialize template for generating mapper header");
        }
        
        logger.info("Loading restricted keyword file from: " + restrictedKeywordsFile);
        try {
            restrictedKeywords = FileUtils.readLines(new File(restrictedKeywordsFile));
        } catch (IOException ioe){
            throw new RKCodeGenException("Error reading restricted keyword file...");
        }
       
        logger.info("Loading attribute type mapping file from: " + attributePropFile);
        try {
            FileInputStream attributePropStream = new FileInputStream(new File(attributePropFile));
            prop.load(attributePropStream);
            
        } catch (IOException ioe){
            throw new RKCodeGenException("Error attribute type mapping file...");
        }

    }

    public void generateCode(InputStream stream) throws RKCodeGenException {
        
        String jsonString = null;

        //Convert stream to string
        try {
            StringWriter stringWriter = new StringWriter();
            
            IOUtils.copy(stream, stringWriter);
            
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

            MorphDynaBean bean = (MorphDynaBean) ooo;

            buildRKObjects(bean, rootObjectName);


        } catch (JSONException e) {
            
            e.printStackTrace();
            throw new RKCodeGenException("Problem parsing JSON...");
            
        }

        if (rkObjects == null || rkObjects.isEmpty()){
            throw new RKCodeGenException("No JSON objects found!");
        }
        
        try {
            
            for (RKObject rkObject : rkObjects) {
                generateHeaderFiles(rkObject);
                generateImplementationFiles(rkObject);
            }
            
            generateMapperHeaderFiles(rkObjects);
            
        } catch (IOException ioe){
            ioe.printStackTrace();
            throw new RKCodeGenException("Error generating files.");
        }
        
    }

    private void buildRKObjects(MorphDynaBean bean, String objectTypeName) {

        MorphDynaClass theClass = (MorphDynaClass) bean.getDynaClass();

        DynaProperty props[] = theClass.getDynaProperties();

        RKObject rkObject = new RKObject();

        //loose the trailing "S"
        objectTypeName = org.apache.commons.lang.StringUtils.removeEndIgnoreCase(objectTypeName, "s");
        
        rkObject.setClassName(getClassName(objectTypeName));

        for (DynaProperty prop : props) {
            //Property is an array
            if (prop.getType().equals(List.class)) {

                List theList = (List) bean.get(prop.getName());

                rkObject.getAttributes().put(processRestrictedKeywords(objectTypeName, prop.getName()), getMappedPropertyType("array"));

                String newObjectTypeName = prop.getName();

                if (!theList.isEmpty()) {

                    MorphDynaBean newBean = (MorphDynaBean) (theList.iterator().next());
                    buildRKObjects(newBean, newObjectTypeName);
                } else {
                    // directly adds an empty RKObject
                    RKObject emptyObject = new RKObject();
                    emptyObject.setClassName(getClassName(newObjectTypeName));
                    this.rkObjects.add(emptyObject);
                }
                
            } 
            
            //Property is an object
            else if (prop.getType().equals(Object.class)){
                
                MorphDynaBean newBean = (MorphDynaBean)bean.get(prop.getName());
                rkObject.getAttributes().put(processRestrictedKeywords(objectTypeName,  prop.getName()), getClassName(prop.getName()));
                rkObject.getImportLines().add(processRestrictedKeywords(objectTypeName,  getClassName(prop.getName())));
                buildRKObjects(newBean, prop.getName());
                
            } 
            
            //Property is "primitive"
            else {

                String propertyPath = objectTypeName + "." + prop.getName();
                rkObject.getAttributes().put(processRestrictedKeywords(objectTypeName, prop.getName()), getMappedPropertyType(propertyPath));
            }
        }
        this.rkObjects.add(rkObject);

    }

    private String getMappedPropertyType(String propertyName) {

        String result = prop.getProperty(propertyName);

        if (result == null) {
            result = prop.getProperty("default");
        }

        return result;

    }

    private void generateObjectFiles(RKObject rkObject, String templateName, String outputFileName) throws IOException {

        VelocityContext context = new VelocityContext();

        context.put("rkObject", rkObject);
        context.put("fileName", outputFileName);

        Template template = getTemplate(templateName);
        
        FileWriter fw = new FileWriter(new File(outputPath + outputFileName));

        template.merge(context, fw);

        fw.close();
        
        logger.info("Successfully generated file: " + outputFileName);
    }

    private void generateHeaderFiles(RKObject rkObject) throws IOException {

        String outputFileName = rkObject.getClassName() + DOT_H;
        
        generateObjectFiles(rkObject, OBJECT_HEADER_TEMPLATE_NAME, outputFileName);
    }

    private void generateImplementationFiles(RKObject rkObject) throws IOException {

        String outputFileName = rkObject.getClassName() + DOT_M;
        
        generateObjectFiles(rkObject, OBJECT_IMPLEMENTATION_TEMPLATE_NAME, outputFileName);
    }
    
    private void generateMapperFiles(List<RKObject> rkObjects, String templateName, String outputFileName) throws IOException {

        VelocityContext context = new VelocityContext();

        context.put("rkObjects", rkObjects);
        context.put("fileName", outputFileName);
        context.put("mapperName", getClassName(rootObjectName) + MAPPER_FILE_SUFFIX);
        Template template = getTemplate(templateName);
        
        
        
        FileWriter fw = new FileWriter(new File(outputPath + outputFileName));

        template.merge(context, fw);

        fw.close();
        
        logger.info("Successfully generated file: " + outputFileName);
    }
    
    private void generateMapperHeaderFiles(List<RKObject> rkObjects) throws IOException {

        String outputFileName = getClassName(rootObjectName) + MAPPER_FILE_SUFFIX + DOT_H;
        
        generateMapperFiles(rkObjects, MAPPER_HEADER_TEMPLATE_NAME, outputFileName);
    }

    private String processRestrictedKeywords(String objectName, String input) {
        if (restrictedKeywords.contains(input)) {
            return objectName + StringUtils.capitalizeFirstLetter(input);
        } else {
            return input;
        }
    }

    private Template getTemplate(String templateName) {
        Template template = null;

        try {
            template = Velocity.getTemplate(templateName);
        } catch (ResourceNotFoundException rnfe) {

            logger.error("Template: " + templateName + " not found, please check template path");
            rnfe.printStackTrace();

        } catch (ParseErrorException pee) {

            logger.error("Template: " + templateName + " cannot be parsed, please check template syntax");
            pee.printStackTrace();

        } catch (MethodInvocationException mie) {

            logger.error("Template: " + templateName + " attempts to invoke unknown method...");
            mie.printStackTrace();

        } catch (Exception e) {

            logger.error("Unknown error loading emplate: " + templateName);
            e.printStackTrace();

        }

        return template;
    }
    
    private String getClassName(String objectTypeName){
        
        String className = StringUtils.capitalizeFirstLetter(objectTypeName);
        
        if (!org.apache.commons.lang.StringUtils.isEmpty(classPrefix)){
            return classPrefix + className;
        } else {
            
            return className;
        }
        
    }

}
