package com.rkgen.jsontorestkitgenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class RKObject {
	
	private String className;
	
	private String mapperName;
	
	private ArrayList<String> importLines;
	
	/**
	 * Mapping of attributeName in the generated class to the attribute Type
	 */
    private Map<String, String> attributes;
    
    /**
     * Mapping of attributeName to the original name in the json file
     */
    private Map<String, String> attributeOriginalNames;
    
    /**
     * Mapping of relationShipAttributes to targetMapperName
     */
    private Map<String, String> relationShipAttributes;

	public RKObject(){
		attributes = new HashMap<String,String>();
		attributeOriginalNames = new HashMap<String,String>();
		importLines = new ArrayList<String>();
		relationShipAttributes = new HashMap<String,String>();
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}
	
	public ArrayList<String> getImportLines() {
        return importLines;
    }

    public void setImportLines(ArrayList<String> importLines) {
        this.importLines = importLines;
    }
	
	public String toString(){
		return new ToStringBuilder( this, ToStringStyle.MULTI_LINE_STYLE ).append( className ).append(attributes).toString();
	}

    public String getMapperName() {
        return mapperName;
    }

    public void setMapperName(String mapperName) {
        this.mapperName = mapperName;
    }


    public Map<String, String> getAttributeOriginalNames() {
        return attributeOriginalNames;
    }

    public void setAttributeOriginalNames(Map<String, String> attributeOriginalNames) {
        this.attributeOriginalNames = attributeOriginalNames;
    }

    public Map<String, String> getRelationshipAttributes() {
        return relationShipAttributes;
    }

    public void setRelationShipAttributes(Map<String, String> relationShipAttributes) {
        this.relationShipAttributes = relationShipAttributes;
    }

}
