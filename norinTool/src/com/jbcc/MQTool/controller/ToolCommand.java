package com.jbcc.MQTool.controller;

/**
 * ���̃N���X���p�����ăR�}���h���������� DB�ɐڑ��A�g�����U�N�V�����Ǘ��@�\���g����悤�ɂȂ�
 * 
 * @author jetbrand
 * 
 */
public abstract class ToolCommand {

	/**
	 * ���̃}�l�[�W�����g����DB�A�N�Z�X����
	 */
	protected ResourceManager RESOURCE;

	void setDbManager(ResourceManager resourceManager) {
		this.RESOURCE = resourceManager;
	}

	// �R�}���h�Ŏ��s����鏈��
	public abstract void execute(String[] args) throws Exception;

}
