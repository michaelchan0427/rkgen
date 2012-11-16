package com.rkgen.jsontorestkitgenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class RKObject {
	
	private String className;
	private ArrayList<String> importLines;
	
    private Map<String, String> attributes;

	public RKObject(){
		attributes = new HashMap<String,String>();
		importLines = new ArrayList<String>();
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
}
