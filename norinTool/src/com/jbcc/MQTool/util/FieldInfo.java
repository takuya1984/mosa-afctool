package com.jbcc.MQTool.util;


public class FieldInfo {
	// const
	protected final static String NUMBER = "NUMBER";
	protected final static String SKIP = "SKIP";
	private String fieldName = null;
	private String type = null;
	private String fieldNameJ = null;
	private int size = 0;
	private int offset = 0;
	private boolean isPrimary = false;



	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public FieldInfo(){
	}

	/**
	 * コンストラクタ
	 *
	 * @param fieldInfo
	 *            フィールド情報
	 */
	public FieldInfo(String fieldInfo) {
		int i = 0;
		String[] values = fieldInfo.trim().replaceAll("[ \\(\\)]", "\t")
				.split("\t");

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

	public int getByteSize() {
		int i = 0;
		if (NUMBER.equals(type)) {
			i = getSize();
			if (i <= 5) {
				i = 2;
			} else if (i <= 10) {
				i = 4;
			} else {
				i = 8;
			}
		} else {
			i = getSize();
		}
		return i;
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

	public int getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = Integer.valueOf(size.split(",")[0].split("¥¥.")[0]);
	}

	public String getFieldNameJ() {
		return fieldNameJ;
	}

	public void setFieldNameJ(String fieldNameJ) {
		this.fieldNameJ = fieldNameJ;
	}

	public String toString() {
		return getFieldName() + ":" + getType() + "(" + getSize() + ") offset:"
				+ getOffset() + " PK:" + isPrimary() + " / " + getFieldNameJ();
	}

	public void setPrimary(boolean isPrimary) {
		this.isPrimary = isPrimary;
	}

	public boolean isPrimary() {
		return isPrimary;
	}

	public boolean isSkip() {
		return getType().equals(SKIP);
	}

	/**
	 * フィールド名称比較<br/>
	 * 内部でしか意味を持たないためcomparableを継承せず、publicにはしない。
	 *
	 * @param fi2
	 *            比較対象
	 * @return FiledName同士のcompare結果
	 */
	protected int compareTo(FieldInfo fi2) {
		return this.getFieldName().compareTo(fi2.getFieldName());
	}
}
