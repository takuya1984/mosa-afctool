package com.jbcc.MQTool.util;

public class FieldInfo {
	String fieldName = null;
	String type = null;
	String size = null;
	String fieldNameJ = null;

	public FieldInfo(String fieldInfo) {
		int i = 0;
		String[] values = fieldInfo.trim().replaceAll("[ \\(\\)]", "\t").split(
				"\t");
		// System.out.println(fieldInfo + ":" + values.length);
		setFieldName(values[i++]);
		setType(values[i++]);
		setSize(values[i++]);
		if (i < values.length) {
			setFieldNameJ(values[i++]);
		}
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getFieldNameJ() {
		return fieldNameJ;
	}

	public void setFieldNameJ(String fieldNameJ) {
		this.fieldNameJ = fieldNameJ;
	}

	public String toString() {
		return getFieldName() + ":" + getType() + "(" + getSize() + ") / "
				+ getFieldNameJ();
	}

}
