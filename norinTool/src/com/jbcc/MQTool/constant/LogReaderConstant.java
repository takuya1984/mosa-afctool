package com.jbcc.MQTool.constant;

public class LogReaderConstant {
	
	// MASTER_ID
	public static final String MASTER_ID_CLIENT = "1";
	public static final String MASTER_ID_WEB = "2";
	public static final String MASTER_ID_OTX_CSS = "3";
	public static final String MASTER_IDO_TX_ONL = "4";
	public static final String MASTER_IDA_PINFO = "5";
	public static final String MASTER_ID_APHOST = "6";
	public static final String MASTER_ID_TRACE = "7";
	public static final String MASTER_ID_DBIO = "8";
	public static final String MASTER_ID_OTX_SQL = "9";
	public static final String MASTER_ID_AP_SQL = "10";
	
	public static final String[] MASTER_ID_LIST = {"12","21","35","53","46","64","78","87","910","109"};
	
	// TODO ローカルパス
	public static final String BASIC_PATH = "C://Documents and Settings//daisuke//デスクトップ//吉田拓//log//";
	
	// 各抽出ログパス
	public static final String CLIENT_PATH = "01_client//";
	public static final String WEB_PATH = "02_web//";
	public static final String OTX_CSS_PATH = "03_otx-css//";
	public static final String OTX_ONL_PATH = "04_otx-onl//";
	public static final String APINFO_PATH = "05_apinfo//";
	public static final String APHOST_PATH = "06_aphost//";
	public static final String TRACE_PATH = "07_trace//";
	public static final String DBIO_PATH = "08_dbio//";
	public static final String OTX_SQL_PATH = "09_otx-sql//";
	public static final String AP_SQL_PATH = "10_ap-sql//";
	
	// 各業務固有部取得キー
	public static final String CLIENT_UP_ONL_KEY = "送信 Onl ";
	public static final String CLIENT_DW_ONL_KEY = "受信 Onl ";
	public static final String CLIENT_UP_CSS_KEY = "送信 Css ";
	public static final String CLIENT_DW_CSS_KEY = "受信 Css ";
	public static final String OTX_UP_ONL_KEY = "上りonl業務固有部=";
	public static final String OTX_DW_ONL_KEY = "下りonl業務固有部=";
	public static final String OTX_UP_CSS_KEY = "上りCSS業務固有部=";
	public static final String OTX_DW_CSS_KEY = "下りCSS業務固有部=";
//	public static final String SOAP_UP_ONL_TAG = "<strSvrUpOnlPrvDt>";
//	public static final String SOAP_DW_ONL_TAG = "<strbufCltDwOnlPrvDt>";
//	public static final String SOAP_UP_CSS_TAG = "<strSvrUpCssPrvDt>";
//	public static final String SOAP_DW_CSS_TAG = "<strbufCltDwCssPrvDt>";
	public static final String SOAP_TAG = "PrvDt>";
	public static final String SQL_KEY = "SQL=<";
	
	
	// 電文種類
	public static final String UP_LOG = "1";
	public static final String DW_LOG = "2";
	public static final String ONL_LOG = "1";
	public static final String CSS_LOG = "2";
	
	
	// 区切り文字
	public static final String DELIMITED_TAB = "\t";
	public static final String DELIMITED_COMMA = ",";
}
