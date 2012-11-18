package com.rkgen.jsontorestkitgenerator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;

import com.rkgen.jsontorestkitgenerator.RKGenConstants;
import com.rkgen.jsontorestkitgenerator.RKObject;
import com.rkgen.jsontorestkitgenerator.RKObjectNamesProvider;
import com.rkgen.jsontorestkitgenerator.TemplatesProvider;
import com.rkgen.jsontorestkitgenerator.exception.RKCodeGenException;

public class RKFilesGenerator {
  
    private static Logger logger = Logger.getLogger("RKCodeGen");
    
    private TemplatesProvider templatesProvider;
    
    private RKObjectNamesProvider namesProvider;
    
    private String outputPath;
    
    public RKFilesGenerator (TemplatesProvider templatesProvider, String outputPath, RKObjectNamesProvider namesProvider) throws RKCodeGenException{
        this.templatesProvider = templatesProvider;
        this.namesProvider = namesProvider;
        logger.info("Checking outputPath...");
        
        String outputPathCopy = new String(outputPath);
        
        File outputPathFile = new File(outputPathCopy);
        
        if (!outputPathFile.isDirectory()){
            throw new RKCodeGenException("Output path must be a directory...");
        }
        
        if (!outputPathFile.isAbsolute()){
            outputPathCopy = outputPathFile.getAbsolutePath();
        }
        
        if (!outputPathCopy.endsWith(RKGenConstants.FILE_SEPARATOR)){
            outputPathCopy = outputPathCopy + RKGenConstants.FILE_SEPARATOR;
        }
        
        this.outputPath = outputPathCopy;
    }
    
    public void generateFiles(List<RKObject> rkObjects, String rootObjectName) throws RKCodeGenException{
        
        try {
            
            for (RKObject rkObject : rkObjects) {
                generateHeaderFiles(rkObject);
                generateImplementationFiles(rkObject);
            }
            
            generateMapperHeaderFiles(rkObjects, rootObjectName);
            generateMapperImplementationFiles(rkObjects, rootObjectName);
            
        } catch (IOException ioe){
            ioe.printStackTrace();
            throw new RKCodeGenException("Error generating files.");
        }
        
    }

    private void generateHeaderFiles(RKObject rkObject) throws IOException {

        String outputFileName = rkObject.getClassName() + RKGenConstants.DOT_H;
        
        generateObjectFiles(rkObject, templatesProvider.getHeaderTemplate(), outputFileName);
    }

    private void generateImplementationFiles(RKObject rkObject) throws IOException {

        String outputFileName = rkObject.getClassName() + RKGenConstants.DOT_M;
        
        generateObjectFiles(rkObject, templatesProvider.getImplementationTemplate(), outputFileName);
    }
    
    
    private void generateObjectFiles(RKObject rkObject, Template templateToUse, String outputFileName) throws IOException {

        VelocityContext context = new VelocityContext();

        context.put(RKGenConstants.CONTEXT_RKOBJECT, rkObject);
        context.put(RKGenConstants.CONTEXT_FILENAME, outputFileName);

        FileWriter fw = new FileWriter(new File(outputPath + outputFileName));

        templateToUse.merge(context, fw);

        fw.close();
        
        logger.info("Successfully generated file: " + outputFileName);
    }
    
    private void generateMapperFiles(List<RKObject> rkObjects, Template templateToUse, String outputFileName, String rootObjectName) throws IOException {

        VelocityContext context = new VelocityContext();

        context.put("rkObjects", rkObjects);
        context.put("fileName", outputFileName);
        context.put("mapperName", namesProvider.getClassName(rootObjectName) + RKGenConstants.MAPPER_CLASS_SUFFIX);
        
        FileWriter fw = new FileWriter(new File(outputPath + outputFileName));

        templateToUse.merge(context, fw);

        fw.close();
        
        logger.info("Successfully generated file: " + outputFileName);
    }
    
    private void generateMapperHeaderFiles(List<RKObject> rkObjects, String rootObjectName) throws IOException {

        String outputFileName = namesProvider.getClassName(rootObjectName) + RKGenConstants.MAPPER_CLASS_SUFFIX + RKGenConstants.DOT_H;
        
        generateMapperFiles(rkObjects, templatesProvider.getMapperHeaderTemplate(), outputFileName, rootObjectName);
    }
    
    private void generateMapperImplementationFiles(List<RKObject> rkObjects, String rootObjectName) throws IOException {

        String outputFileName = namesProvider.getClassName(rootObjectName) + RKGenConstants.MAPPER_CLASS_SUFFIX + RKGenConstants.DOT_M;
        
        generateMapperFiles(rkObjects, templatesProvider.getMapperImplementationTemplate(), outputFileName, rootObjectName);
    }

}
