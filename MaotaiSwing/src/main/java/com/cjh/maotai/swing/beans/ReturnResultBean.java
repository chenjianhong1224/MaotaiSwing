package com.cjh.maotai.swing.beans;

import java.io.Serializable;

public class ReturnResultBean implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5172597012141921377L;

	private int resultCode;
	
	private String returnMsg;
	
	private Object returnObj;

	public int getResultCode() {
		return resultCode;
	}

	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}

	public String getReturnMsg() {
		return returnMsg;
	}

	public void setReturnMsg(String returnMsg) {
		this.returnMsg = returnMsg;
	}

	public Object getReturnObj() {
		return returnObj;
	}

	public void setReturnObj(Object returnObj) {
		this.returnObj = returnObj;
	}

}
