package com.jbcc.MQTool.controller;

import com.jbcc.MQTool.util.StdOut;

public class EntryPoint {

	private final static int RESULT_SUCCESS = 0;
	private final static int RESULT_ERROR = 1;
	private final static int RESULT_EXCEPTION = -1;

	/**
	 * ���̃A�v���̃G���g���[�|�C���g ���\�[�X�Ǘ��Ɨ�O�n���h�����O���s��
	 * 
	 * �R�}���h���C���������̕�����ŁA���s�R�}���h�N���X���C���X�^���X�������s����B
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		int result = RESULT_SUCCESS;
		ResourceManager rcmng = new ResourceManager();
		try {

			// �����`�F�b�N
			if (args.length < 1) {
				StdOut.write("�������s���ł��B�������͎��s�N���X��");
				System.exit(RESULT_ERROR);
				return;
			}

			// �R�}���h�����̐���
			String commandName = args[0];
			if (commandName.endsWith("_d")) {
				StdOut.isDebug = true;
				commandName = commandName.replace("_d", "");
			}
			ToolCommand cmd = getCommand(commandName);
			cmd.setDbManager(rcmng);

			result = cmd.execute(args);

		} catch (ToolException te) {

			// �R�}���h�ŏ������ꂽ��O ���b�Z�[�W���o�͂��āA�G���[�ŏI��
			te.printStackTrace();
			try {
				rcmng.rollback();
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.exit(RESULT_ERROR);
			return;

		} catch (Exception e) {

			// �n���h�����O����Ȃ���O���W�񂵂ăL���b�`
			e.printStackTrace();
			try {
				rcmng.rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			System.exit(RESULT_EXCEPTION);
			return;

		} finally {

			// ���\�[�X���J��
			try {
				rcmng.commit();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		System.exit(result);
	}

	private static ToolCommand getCommand(String name) throws Exception {

		try {
			Class<?> clazz = Class.forName("com.jbcc.MQTool.commands." + name);
			return (ToolCommand) clazz.newInstance();
		} catch (Exception e) {
			throw new IllegalArgumentException("�R�}���h���s�N���X�̎擾�Ɏ��s���܂����B", e);
		}

	}

}