package com.jbcc.MQTool.controller;

/**
 * このクラスを継承してコマンドを実装する DBに接続、トランザクション管理機能が使えるようになる
 * 
 * @author jetbrand
 * 
 */
public abstract class ToolCommand {

	/**
	 * このマネージャを使ってDBアクセスする
	 */
	protected ResourceManager RESOURCE;

	void setDbManager(ResourceManager resourceManager) {
		this.RESOURCE = resourceManager;
	}

	// コマンドで実行される処理
	public abstract void execute(String[] args) throws Exception;

}
