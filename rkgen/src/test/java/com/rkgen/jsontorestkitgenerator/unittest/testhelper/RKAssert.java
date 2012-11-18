package com.rkgen.jsontorestkitgenerator.unittest.testhelper;

import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import com.rkgen.jsontorestkitgenerator.RKObject;

public class RKAssert {
    
    public static void assertRKObject(RKObject rkObject, String expectedClassName, String expectedMapperName, Map<String, String> expectedAttributesAndType, 
            Map<String, String> expectedRelationShipAttr){
        
        Assert.assertEquals(expectedClassName, rkObject.getClassName());
        
        Assert.assertEquals(expectedMapperName, rkObject.getMapperName());
        
        Set<String> expAttrKey = expectedAttributesAndType.keySet();
        
        Map<String, String> attr = rkObject.getAttributes();
        
        Assert.assertEquals(expectedAttributesAndType.size(), attr.size());
        
        for (String attrKey : expAttrKey){
            
            Assert.assertEquals(expectedAttributesAndType.get(attrKey), attr.get(attrKey));
            
        }
        
        
        if (expectedRelationShipAttr != null && !expectedRelationShipAttr.isEmpty()){
            
            Assert.assertEquals(expectedRelationShipAttr.size(), rkObject.getRelationshipAttributes().size());
            
            Set<String> relKeys = expectedRelationShipAttr.keySet();
            
            
            for (String attrKey : relKeys){
                
                Assert.assertEquals(expectedRelationShipAttr.get(attrKey), rkObject.getRelationshipAttributes().get(attrKey));
                
            }
        } else {
            
            Assert.assertEquals(0, rkObject.getRelationshipAttributes().size());
        }
        
    }

}
