package com.rkgen.jsontorestkitgenerator;

public class RKGenConstants {

    public static final String DOT = ".";
    public static final String DOT_H = ".h";
    public static final String DOT_M = ".m";
    
    public static final String FILE_SEPARATOR = System.getProperty("file.separator");
    
    public static final String MAPPER_CLASS_SUFFIX = "Mapper";
    
    //Template names
    public static final String OBJECT_HEADER_TEMPLATE_NAME = "headertemplate.vm";
    public static final String OBJECT_IMPLEMENTATION_TEMPLATE_NAME = "implementationtemplate.vm";
    public static final String MAPPER_HEADER_TEMPLATE_NAME = "mapperHeaderTemplate.vm";
    public static final String MAPPER_IMPLEMENTATION_TEMPLATE_NAME = "mapperImplementationTemplate.vm";
    
    //keys for look up in restricted properties file
    public static final String DEFAULT_PROPERTY_KEY = "default";
    public static final String ARRAY_PROPERTY_KEY = "array";
    
    
    //velocity context attribute names
    public static final String CONTEXT_RKOBJECT = "rkObject";
    public static final String CONTEXT_FILENAME = "fileName";
    public static final String CONTEXT_RKOBJECTS = "rkObjects";
    public static final String CONTEXT_MAPPER_CLASS_NAME = "mapperName";
    
}
