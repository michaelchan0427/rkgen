package com.rkgen.jsontorestkitgenerator;

import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeConstants;

import com.rkgen.jsontorestkitgenerator.exception.RKCodeGenException;

public class TemplatesProvider {

    private static Logger logger = Logger.getLogger("RKCodeGen");
    
    private Template headerTemplate;
    private Template implementationTemplate;
    private Template mapperImplementationTemplate;
    private Template mapperHeaderTemplate;
    
    public TemplatesProvider (String templatePath) throws RKCodeGenException{
        
        logger.info("Initializing Velocity...");

        Velocity.setProperty(RuntimeConstants.RESOURCE_LOADER, "file");
        Velocity.setProperty("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.FileResourceLoader");
        logger.info("Setting Velocity Template path to: " + templatePath);
        Velocity.setProperty("file.resource.loader.path", templatePath);
        Velocity.init();
        
        getHeaderTemplate();
        
        if (headerTemplate == null) {
            throw new RKCodeGenException("Cannot initialize template for generating object header");
        }

        getImplementationTemplate();
        
        if (implementationTemplate == null) {
            throw new RKCodeGenException("Cannot initialize template for generating object implementation");
        }
        
        getMapperHeaderTemplate();

        if (mapperHeaderTemplate == null) {
            throw new RKCodeGenException("Cannot initialize template for generating mapper header");
        }
        
        getMapperImplementationTemplate();
        
        if (mapperImplementationTemplate == null) {
            throw new RKCodeGenException("Cannot initialize template for generating mapper implementation");
        }
        
    }

    public Template getHeaderTemplate(){
        if (headerTemplate == null){
            headerTemplate = getTemplate(RKGenConstants.OBJECT_HEADER_TEMPLATE_NAME);
        }
        return headerTemplate;
    }
    
    public Template getImplementationTemplate(){
        if (implementationTemplate == null){
            implementationTemplate = getTemplate(RKGenConstants.OBJECT_IMPLEMENTATION_TEMPLATE_NAME);
        }
        return implementationTemplate;
    }
    
    public Template getMapperHeaderTemplate(){
        
        if (mapperHeaderTemplate == null){
            mapperHeaderTemplate = getTemplate(RKGenConstants.MAPPER_HEADER_TEMPLATE_NAME);
        }
        return mapperHeaderTemplate;
    }
    
    public Template getMapperImplementationTemplate(){
        
        if (mapperImplementationTemplate == null){
            mapperImplementationTemplate = getTemplate(RKGenConstants.MAPPER_IMPLEMENTATION_TEMPLATE_NAME);
        }
        return mapperImplementationTemplate;
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
}
