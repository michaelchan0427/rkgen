package com.rkgen.jsontorestkitgenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


public class RKGen {

    private static Logger logger = Logger.getLogger("RKCodeGen");
    
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		
	    if (args.length < 6) {
	        logger.error("Usage: java -jar RKCodeGen.jar " +
	        		"[json file path / URL] " +
	        		"[output path] " +
	        		"[root object name] " +
	        		"[template path] " +
	        		"[attribute type mapping file] " +
	        		"[restricted keywords file] " +
	        		"[Class prefix](optional)" );
	        
	    }
	    String classPrefix = null;
	    if (args.length == 7){
	        classPrefix = args[6];
	    }
	    
		RKCodeGen codeGen = new RKCodeGen(args[3], args[4], args[5], args[1], args[2], classPrefix);
		
		InputStream inputStream = null;
		
		if (isHttpPath(args[0])){
		    logger.info("Generating Objective-C files based on URL: " + args[0]);
		    inputStream = JsonRequestHelper.getJsonInputStream(args[0]);
		} else {
		    logger.info("Generating Objective-C files based on file: " + args[0]);
		    inputStream = new FileInputStream(new File(args[0]));
		}
		
		
		codeGen.generateCode(inputStream);

	}
	
	private static boolean isHttpPath(String path){
	    
	    return (StringUtils.startsWith(path, "HTTP") || StringUtils.startsWith(path, "http"));
	    
	}

}
