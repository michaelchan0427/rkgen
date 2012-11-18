package com.rkgen.jsontorestkitgenerator.unittest.BasicTest;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;
import net.sf.ezmorph.bean.MorphDynaBean;

import org.junit.Test;

import com.rkgen.jsontorestkitgenerator.RKCodeGen;
import com.rkgen.jsontorestkitgenerator.RKObject;
import com.rkgen.jsontorestkitgenerator.unittest.testhelper.RKAssert;

/**
 *  Testing JSON with the following structure 
 * 
 *
 * {
      "catalogs": [
        {
          "id": "electronicsProductCatalog",
          "lastModified": "2012-08-25T11:04:07+01:00",
          "name": "Electronics Product Catalog",
          "url": "/electronicsProductCatalog",
          "user": {
          
            "name": "dasfasdf",
            "password" : "pass"
          },
          "catalogVersions": [
            {
              "id": "Staged",
              "lastModified": "2012-08-25T11:05:33+01:00",
              "url": "/electronicsProductCatalog/Staged",
              "categoriesHierarchyData": []
            },
            {
              "id": "Online",
              "lastModified": "2012-08-31T15:57:34+01:00",
              "url": "/electronicsProductCatalog/Online",
              "categoriesHierarchyData": []
            }
          ]
        }
      ]
    }
*/
public class TestGeneratedObject {
    
   /**
     * @throws Exception
     */
    
    
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
        
        RKCodeGen codeGen = new RKCodeGen(templatesPath, attributeTypesPath, restrictedKeywordsPath, outputPath, rootObjectName, "TT");
        
        MorphDynaBean bean = codeGen.convertInputStreamToMorphDynaBean(new FileInputStream(jsonPath));
        
        List<RKObject> rkObjects = codeGen.getObjectBuilder().generateRKObjects(bean, rootObjectName);
        
        //Should only contain five elements
        
        Assert.assertEquals(5, rkObjects.size());
        
        //Test the last object, should be "TTTest"
        Map<String, String> expectedAttributesAndType = new HashMap<String, String>();
        
        expectedAttributesAndType.put("catalogs", "NSMutableArray");
        
        Map<String, String> expectedRelationShipAttr = new HashMap<String, String>();
        
        expectedRelationShipAttr.put("catalogs", "tTCatalogMapper");
        
        RKAssert.assertRKObject(rkObjects.get(4), "TTTest", "tTTestMapper", expectedAttributesAndType, expectedRelationShipAttr);
        
        
        //Second last, should be catalog
        RKObject catalogObject = rkObjects.get(3);
        
        expectedAttributesAndType = new HashMap<String, String>();
        expectedRelationShipAttr = new HashMap<String, String>();
        
        expectedAttributesAndType.put("catalogsId", "NSString");
        expectedAttributesAndType.put("lastModified", "NSDate");
        expectedAttributesAndType.put("name", "NSString");
        expectedAttributesAndType.put("url", "NSString");
        expectedAttributesAndType.put("user", "TTUser");
        expectedAttributesAndType.put("catalogVersions", "NSMutableArray");
        
        expectedRelationShipAttr.put("user","tTUserMapper");
        expectedRelationShipAttr.put("catalogVersions","tTCatalogVersionMapper");
        
        RKAssert.assertRKObject(catalogObject, "TTCatalog", "tTCatalogMapper", expectedAttributesAndType, expectedRelationShipAttr);
        
        //The 3rd object, should be user
        RKObject userObject = rkObjects.get(2);
        
        expectedAttributesAndType = new HashMap<String, String>();
        expectedRelationShipAttr = new HashMap<String, String>();
        
        expectedAttributesAndType.put("name", "NSString");
        expectedAttributesAndType.put("password", "NSString");
        
        RKAssert.assertRKObject(userObject, "TTUser", "tTUserMapper", expectedAttributesAndType, null);
        
        RKObject catalogVersionsObject = rkObjects.get(1);
        
        expectedAttributesAndType = new HashMap<String, String>();
        expectedRelationShipAttr = new HashMap<String, String>();
        
        
        expectedAttributesAndType.put("catalogVersionsId", "NSString");
        expectedAttributesAndType.put("lastModified", "NSDate");
        expectedAttributesAndType.put("url", "NSString");
        expectedAttributesAndType.put("categoriesHierarchyData", "NSMutableArray");
        
        expectedRelationShipAttr.put("categoriesHierarchyData","tTCategoriesHierarchyDataMapper");
        
        RKAssert.assertRKObject(catalogVersionsObject, "TTCatalogVersion", "tTCatalogVersionMapper", expectedAttributesAndType, expectedRelationShipAttr);
        
        RKObject categoriesHierarchyData = rkObjects.get(0);
        
        expectedAttributesAndType = new HashMap<String, String>();
        
        RKAssert.assertRKObject(categoriesHierarchyData, "TTCategoriesHierarchyData", "tTCategoriesHierarchyDataMapper", expectedAttributesAndType, null);
        
    }
    
}
