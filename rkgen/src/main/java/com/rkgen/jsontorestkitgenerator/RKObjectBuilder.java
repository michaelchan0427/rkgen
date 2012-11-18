package com.rkgen.jsontorestkitgenerator;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.DynaProperty;

import net.sf.ezmorph.bean.MorphDynaBean;
import net.sf.ezmorph.bean.MorphDynaClass;

public class RKObjectBuilder {

    private ArrayList<RKObject> rkObjects;
    
    
    
    private RKObjectNamesProvider namesProvider;
    
    public RKObjectBuilder(RKObjectNamesProvider namesProvider){
        
        this.namesProvider = namesProvider;
        
        rkObjects = new ArrayList<RKObject>();
        
    }
    
    public List<RKObject> generateRKObjects(MorphDynaBean morphDynaBean, String rootObjectName){
        
        buildRKObjects(morphDynaBean, rootObjectName);
        
        return this.rkObjects;
        
    }
    
    
    private void buildRKObjects(MorphDynaBean morphDynaBean, String jsonObjectName) {

        MorphDynaClass theClass = (MorphDynaClass) morphDynaBean.getDynaClass();

        DynaProperty props[] = theClass.getDynaProperties();

        RKObject rkObject = new RKObject();

        rkObject.setClassName(namesProvider.getClassName(jsonObjectName));
        rkObject.setMapperName(namesProvider.getMapperName(jsonObjectName));
        
        
        for (DynaProperty prop : props) {
            
            String generatedPropertyName = namesProvider.getPropertyName(jsonObjectName, prop.getName());
            
            rkObject.getAttributeOriginalNames().put(generatedPropertyName, prop.getName());
            
            //Property is an array
            if (prop.getType().equals(List.class)) {

                List theList = (List) morphDynaBean.get(prop.getName());
                
                rkObject.getAttributes().put(generatedPropertyName, namesProvider.getPropertyTypeName(null, RKGenConstants.ARRAY_PROPERTY_KEY));
                
                //Gets the Mapper name of the target object on the other end of the relationship.
                rkObject.getRelationshipAttributes().put(generatedPropertyName, namesProvider.getMapperName(prop.getName()));
                
                if (!theList.isEmpty()) {

                    MorphDynaBean newBean = (MorphDynaBean) (theList.iterator().next());
                    buildRKObjects(newBean, prop.getName());
                    
                } else {
                    // directly adds an empty RKObject
                    RKObject emptyObject = new RKObject();
                    emptyObject.setClassName(namesProvider.getClassName(prop.getName()));
                    emptyObject.setMapperName(namesProvider.getMapperName(prop.getName()));
                    this.rkObjects.add(emptyObject);
                }
                
            } 
            
            //Property is an object
            else if (prop.getType().equals(Object.class)){
                
                MorphDynaBean newBean = (MorphDynaBean)morphDynaBean.get(prop.getName());
                //Property type is the classname in this case, therefore, don't need to invoke namesProvider.getPropertyTypeName
                rkObject.getAttributes().put(generatedPropertyName, namesProvider.getClassName(prop.getName()));
                rkObject.getRelationshipAttributes().put(generatedPropertyName, namesProvider.getMapperName(prop.getName()));
                rkObject.getImportLines().add(namesProvider.getClassName(prop.getName()));
                buildRKObjects(newBean, prop.getName());
                
            } 
            
            //Property is other types, could be primitive or something else
            else {

                rkObject.getAttributes().put(generatedPropertyName, namesProvider.getPropertyTypeName(jsonObjectName, prop.getName()));
            }
        }
        this.rkObjects.add(rkObject);

    }
    
}
