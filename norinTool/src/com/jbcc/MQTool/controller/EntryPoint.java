package com.jbcc.MQTool.controller;

import com.jbcc.MQTool.util.StdOut;

public class EntryPoint {

	private final static int RESULT_SUCCESS = 0;
	private final static int RESULT_ERROR = 1;
	private final static int RESULT_EXCEPTION = -1;

	/**
	 * このアプリのエントリーポイント リソース管理と例外ハンドリングを行う
	 * 
	 * コマンドライン第一引数の文字列で、実行コマンドクラスをインスタンス化し実行する。
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		int result = RESULT_SUCCESS;
		ResourceManager rcmng = new ResourceManager();
		try {

			// 引数チェック
			if (args.length < 1) {
				StdOut.write("引数が不正です。第一引数は実行クラス名");
				System.exit(RESULT_ERROR);
				return;
			}

			// コマンド処理の制御
			String commandName = args[0];
			if (commandName.endsWith("_d")) {
				StdOut.isDebug = true;
				commandName = commandName.replace("_d", "");
			}
			ToolCommand cmd = getCommand(commandName);
			cmd.setDbManager(rcmng);

			cmd.execute(args);

		} catch (ToolException te) {

			// コマンドで処理された例外 メッセージを出力して、エラーで終了
			te.printStackTrace();
			try {
				rcmng.rollback();
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.exit(RESULT_ERROR);
			return;

		} catch (Exception e) {

			// ハンドリングされない例外を集約してキャッチ
			e.printStackTrace();
			try {
				rcmng.rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			System.exit(RESULT_EXCEPTION);
			return;

		} finally {

			// リソースを開放
			rcmng.release();
		}

		System.exit(result);
	}

	private static ToolCommand getCommand(String name) throws Exception {

		try {
			Class<?> clazz = Class.forName("com.jbcc.MQTool.commands." + name);
			return (ToolCommand) clazz.newInstance();
		} catch (Exception e) {
			throw new IllegalArgumentException("コマンド実行クラスの取得に失敗しました。", e);
		}

	}

}