package com.cjh.maotai.swing.beans;

import java.io.Serializable;

public class MaotaiOrderSend implements Serializable {
	private String deliveryId;
	private String addressId;
	public String getDeliveryId() {
		return deliveryId;
	}
	public void setDeliveryId(String deliveryId) {
		this.deliveryId = deliveryId;
	}
	public String getAddressId() {
		return addressId;
	}
	public void setAddressId(String addressId) {
		this.addressId = addressId;
	}

}
