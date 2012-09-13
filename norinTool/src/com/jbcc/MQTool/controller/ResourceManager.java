package com.jbcc.MQTool.controller;

import java.io.Closeable;
import java.io.FileInputStream;
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
		// �p�b�P�[�W�O��new�֎~
	}

	private Connection con;
	private List<Closeable> streams = new ArrayList<Closeable>();

	/**
	 * �t�@�C��stream��DB�ڑ����J������
	 */
	void release() {

		// �t�@�C���̊J��
		for (Closeable stream : streams) {
			try {
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// DB�R�l�N�V�����̊J��
		try {
			if (con != null) {
				con.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * FileInputStream���擾���� �v���Z�X�I�����ɊJ�������
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
	 * FileOutputStream���擾���� �v���Z�X�I�����ɊJ�������
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
	 * select�̎��s
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

			// SQL�̎��s
			st = getConnection().prepareStatement(sql);
			StdOut.writeDebug(sql);
			int paramCount = 1;
			for (Object param : params) {
				st.setObject(paramCount, param);
				paramCount++;
			}
			result = st.executeQuery();

			// ���� �\�C���[�W�̍쐬
			List<Map<String, Object>> queryResult = new ArrayList<Map<String, Object>>();
			ResultSetMetaData meta = result.getMetaData();
			int columunCount = meta.getColumnCount();

			// ��s���������ʃf�[�^���쐬���Ă������[�v
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
	 * preparedstatement��insert update delete�����s
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

			// SQL�̎��s
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
	 * �R�l�N�V�������擾
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
	 * DB�g�����U�N�V�����̃R�~�b�g
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
	 * DB�g�����U�N�V�����̃��[���o�b�N
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

	// �v���p�e�B�Q
	private Map<String, Properties> sqlProperty = new HashMap<String, Properties>();

	/**
	 * �v���p�e�B�t�@�C������l�擾
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
	 * SQL���擾
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public String getSql(String id) throws Exception {

		return getProperty("sql.properties", id);

	}
}
