package com.jbcc.MQTool.controller;


public class ToolException extends Exception {
	
	private int errcode;
	
	public int getErrcode() {
		return errcode;
	}

	private static final long serialVersionUID = 1L;

	public ToolException(int errcode){
		this.errcode = errcode;
	}
	
	public ToolException(String message){
		super(message);
	}
}
