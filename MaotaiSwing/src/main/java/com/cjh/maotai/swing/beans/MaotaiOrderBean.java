package com.cjh.maotai.swing.beans;

import java.io.Serializable;
import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;

public class MaotaiOrderBean implements Serializable{
	@JSONField(name = "orderBase")
	private MaotaiOrderBase maotaiOrderBase;
	@JSONField(name = "orderSend")
	private MaotaiOrderSend maotaiOrderSend;
	@JSONField(name = "orderItems")
	private List<MaotaiOrderItem> maotaiOrderItems;
	private Integer voucherId;
	private Integer purchaseWay;
	public Integer getVoucherId() {
		return voucherId;
	}
	public void setVoucherId(Integer voucherId) {
		this.voucherId = voucherId;
	}
	public Integer getPurchaseWay() {
		return purchaseWay;
	}
	public void setPurchaseWay(Integer purchaseWay) {
		this.purchaseWay = purchaseWay;
	}
	public MaotaiOrderBase getMaotaiOrderBase() {
		return maotaiOrderBase;
	}
	public void setMaotaiOrderBase(MaotaiOrderBase maotaiOrderBase) {
		this.maotaiOrderBase = maotaiOrderBase;
	}
	public MaotaiOrderSend getMaotaiOrderSend() {
		return maotaiOrderSend;
	}
	public void setMaotaiOrderSend(MaotaiOrderSend maotaiOrderSend) {
		this.maotaiOrderSend = maotaiOrderSend;
	}
	public List<MaotaiOrderItem> getMaotaiOrderItems() {
		return maotaiOrderItems;
	}
	public void setMaotaiOrderItems(List<MaotaiOrderItem> maotaiOrderItems) {
		this.maotaiOrderItems = maotaiOrderItems;
	}

}
