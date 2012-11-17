package com.rkgen.jsontorestkitgenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.util.StringUtils;

import com.rkgen.jsontorestkitgenerator.util.RKStringUtil;

public class RKObjectNamesProvider {
    
    private static Logger logger = Logger.getLogger("RKCodeGen");
    
    private Properties prop = new Properties();
    
    private List<String> restrictedKeywords = null;
    
    private String classPrefix = null;
    
    public RKObjectNamesProvider(String attributePropFilePath, String restrictedKeywordsFilePath, String classPrefix) throws IOException{
        logger.info("Loading attribute type mapping file from: " + attributePropFilePath);
        try {
            FileInputStream attributePropStream = new FileInputStream(new File(attributePropFilePath));
            prop.load(attributePropStream);
            
        } catch (IOException ioe){
            throw new IOException("Error in reading attribute type mapping file... " + attributePropFilePath );
        }
        
        logger.info("Loading restricted keyword file from: " + restrictedKeywordsFilePath);
        try {
            restrictedKeywords = FileUtils.readLines(new File(restrictedKeywordsFilePath));
        } catch (IOException ioe){
            throw new IOException("Error reading restricted keyword file...");
        }
        
        this.classPrefix = classPrefix;
    }
    
    public String getClassName (String jsonPropertyName){
        
        String className = StringUtils.capitalizeFirstLetter(jsonPropertyName);
        
        //loose the trailing "S"
        className = org.apache.commons.lang.StringUtils.removeEndIgnoreCase(className, "s");
        if (!org.apache.commons.lang.StringUtils.isEmpty(classPrefix)){
            return classPrefix + className;
        } else {
            
            return className;
        }
        
    }
    
    public String getPropertyName (String jsonObjectName, String jsonPropertyName){
        if (restrictedKeywords.contains(jsonPropertyName)) {
            return jsonObjectName + StringUtils.capitalizeFirstLetter(jsonPropertyName);
        } else {
            return jsonPropertyName;
        }
    }

    public String getPropertyTypeName (String jsonObjectName, String propertyName) {
        
        String result = null;
        
        if (jsonObjectName == null){
            result = prop.getProperty(propertyName);
        } else {
            result = prop.getProperty(jsonObjectName + RKGenConstants.DOT + propertyName);
        }
                
        
        if (result == null) {
            result = prop.getProperty(RKGenConstants.DEFAULT_PROPERTY_KEY);
        }

        return result;
    }
    
    
    public String getMapperName (String jsonObjectName){
        
        String className = this.getClassName(jsonObjectName);
        
        return RKStringUtil.lowerCaseFirstLetter(className) + RKGenConstants.MAPPER_CLASS_SUFFIX;
        
    }
}
