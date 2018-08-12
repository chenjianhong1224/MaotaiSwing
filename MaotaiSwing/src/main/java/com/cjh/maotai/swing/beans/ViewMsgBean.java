package com.cjh.maotai.swing.beans;

import java.io.Serializable;
import java.util.Date;

public class ViewMsgBean implements Serializable {
	
	private String msg;
	
	private Date time;
	
	private String taskNo;

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getTaskNo() {
		return taskNo;
	}

	public void setTaskNo(String taskNo) {
		this.taskNo = taskNo;
	}

}
