package com.jbcc.MQTool.controller;

import java.io.Closeable;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.jbcc.MQTool.util.StdOut;

public class ResourceManager {

	private final String CONNECTION_PROPERTY_NAME = "connection.properties";

	ResourceManager() {
		// パッケージ外でnew禁止
	}

	private Connection con;
	private List<Closeable> streams = new ArrayList<Closeable>();

	/**
	 * ファイルstreamとDB接続を開放する
	 */
	void release() {

		// ファイルの開放
		for (Closeable stream : streams) {
			try {
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// DBコネクションの開放
		try {
			if (con != null) {
				con.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * FileInputStreamを取得する プロセス終了時に開放される
	 * 
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public InputStream getInputStream(String path) throws Exception {

		InputStream stm = new FileInputStream(path);
		streams.add(stm);
		return stm;

	}

	/**
	 * FileOutputStreamを取得する プロセス終了時に開放される
	 * 
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public OutputStream getOutputStream(String path) throws Exception {

		OutputStream stm = new FileOutputStream(path);
		streams.add(stm);
		return stm;

	}

	/**
	 * selectの実行
	 * 
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> selectDB(String sql, Object[] params)
			throws Exception {

		PreparedStatement st = null;
		ResultSet result = null;

		try {

			// SQLの実行
			st = getConnection().prepareStatement(sql);
			StdOut.writeDebug(sql);
			int paramCount = 1;
			for (Object param : params) {
				st.setObject(paramCount, param);
				paramCount++;
			}
			result = st.executeQuery();

			// 結果 表イメージの作成
			List<Map<String, Object>> queryResult = new ArrayList<Map<String, Object>>();
			ResultSetMetaData meta = result.getMetaData();
			int columunCount = meta.getColumnCount();

			// 一行ずつ検索結果データを作成していくループ
			while (result.next()) {
				Map<String, Object> row = new HashMap<String, Object>();
				queryResult.add(row);
				for (int i = 1; i <= columunCount; i++) {
					String colName = meta.getColumnName(i);
					row.put(colName, result.getObject(colName));
				}
			}
			return queryResult;

		} finally {
			try {
				result.close();
				st.close();
			} catch (Exception e) {
				// e.printStackTrace();
			}
		}

	}

	/**
	 * preparedstatementでinsert update deleteを実行
	 * 
	 * @param sql
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public int updateDB(String sql, Object[] params) throws Exception {

		PreparedStatement st = null;
		int count = 0;
		try {

			// SQLの実行
			st = getConnection().prepareStatement(sql);
			StdOut.writeDebug(sql);

			int i = 1;
			for (Object param : params) {
				st.setObject(i, param);
				i++;
			}
			count = st.executeUpdate();

		} finally {
			try {
				st.close();
			} catch (Exception e) {
				// e.printStackTrace();
			}
		}

		return count;
	}

	/**
	 * コネクションを取得
	 * 
	 * @return
	 * @throws Exception
	 */
	private Connection getConnection() throws Exception {
		if (con == null) {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = DriverManager.getConnection(
					getProperty(CONNECTION_PROPERTY_NAME, "connectionString"),
					getProperty(CONNECTION_PROPERTY_NAME, "user"),
					getProperty(CONNECTION_PROPERTY_NAME, "password"));

		}
		return con;
	}

	/**
	 * DBトランザクションのコミット
	 * 
	 * @throws Exception
	 */
	void commit() throws Exception {
		if (con != null) {
			con.commit();
			con.close();
			con = null;
		}

	}

	/**
	 * DBトランザクションのロールバック
	 * 
	 * @throws Exception
	 */
	void rollback() throws Exception {
		if (con != null) {
			con.rollback();
			con.close();
			con = null;
		}
	}

	// プロパティ群
	private Map<String, Properties> sqlProperty = new HashMap<String, Properties>();

	/**
	 * プロパティファイルから値取得
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public String getProperty(String propFilename, String id) throws Exception {

		if (!sqlProperty.containsKey(propFilename)) {
			InputStream stream = this.getClass().getClassLoader()
					.getResourceAsStream(propFilename);
			try {
				Properties prop = new Properties();
				prop.loadFromXML(stream);
				sqlProperty.put(propFilename, prop);
			} finally {
				stream.close();
			}
		}
		return sqlProperty.get(propFilename).getProperty(id);

	}

	/**
	 * SQLを取得
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public String getSql(String id) throws Exception {

		return getProperty("sql.properties", id);

	}
}
