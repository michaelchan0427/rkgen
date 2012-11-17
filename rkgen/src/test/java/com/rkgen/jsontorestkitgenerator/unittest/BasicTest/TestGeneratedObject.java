package com.rkgen.jsontorestkitgenerator.unittest.BasicTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import net.sf.ezmorph.bean.MorphDynaBean;

import org.junit.Test;

import com.rkgen.jsontorestkitgenerator.JsonRequestHelper;
import com.rkgen.jsontorestkitgenerator.RKCodeGen;
import com.rkgen.jsontorestkitgenerator.RKObject;

public class TestGeneratedObject {
    
    ///Developments/Git/rkgen/rkgen/src/main/resources/example_jsons/example.json 
    ///Developments/Git/rkgen/rkgen/output 
    //Product 
    ///Developments/Git/rkgen/rkgen/src/main/resources/templates 
    ///Developments/Git/rkgen/rkgen/src/main/resources/conf/attributetypes.properties 
    ///Developments/Git/rkgen/rkgen/src/main/resources/conf/restrictedKeywords HY
    
    @Test
    public void testRKCodeGen() throws Exception{
        
        
        URL url = this.getClass().getResource("example.json");
        String filePath = url.getPath();
        int index = filePath.lastIndexOf(File.separatorChar);
        String path = filePath.substring(0, index);
        
        String jsonPath = path + "/example.json";
        String outputPath = System.getProperty("java.io.tmpdir");
        
        String templatesPath = path;
        
        String rootObjectName = "Test";
        
        String attributeTypesPath = path + "/attributetypes.properties";
        
        String restrictedKeywordsPath = path + "/restrictedKeywords";
        
//        RKCodeGen codeGen = new RKCodeGen(templatesPath, attributeTypesPath, restrictedKeywordsPath, outputPath, rootObjectName, TT);
//        
//        
//        MorphDynaBean bean = codeGen.convertInputStreamToMorphDynaBean(stream);
//        
//        List<RKObject> objects = buildRKObjects(bean, rootObjectName);
        
    }
    
}
