package com.cjh.maotai.swing.beans;

import java.io.Serializable;

public class MaotaiOrderBase implements Serializable{
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getShopId() {
		return shopId;
	}
	public void setShopId(String shopId) {
		this.shopId = shopId;
	}
	private String remark;
	private String shopId;
}
