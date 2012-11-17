package com.rkgen.jsontorestkitgenerator.util;

public class RKStringUtil {
    public static  String lowerCaseFirstLetter(String str){
        
        char firstLetter = str.charAt(0);
        
        String result = str.substring(1);
        
        
        return org.apache.commons.lang.StringUtils.lowerCase(firstLetter+"") + result;
        
    }
}
