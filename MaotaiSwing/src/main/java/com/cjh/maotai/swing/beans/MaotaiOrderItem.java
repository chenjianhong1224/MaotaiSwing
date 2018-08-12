package com.cjh.maotai.swing.beans;

import java.io.Serializable;

public class MaotaiOrderItem implements Serializable {
	private String itemId;
	private String itemNum;
	private String shopId;
	private String skuId;
	private String itemPrice;
	private String isPartialShipment;
	public String getItemId() {
		return itemId;
	}
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	public String getItemNum() {
		return itemNum;
	}
	public void setItemNum(String itemNum) {
		this.itemNum = itemNum;
	}
	public String getShopId() {
		return shopId;
	}
	public void setShopId(String shopId) {
		this.shopId = shopId;
	}
	public String getSkuId() {
		return skuId;
	}
	public void setSkuId(String skuId) {
		this.skuId = skuId;
	}
	public String getItemPrice() {
		return itemPrice;
	}
	public void setItemPrice(String itemPrice) {
		this.itemPrice = itemPrice;
	}
	public String getIsPartialShipment() {
		return isPartialShipment;
	}
	public void setIsPartialShipment(String isPartialShipment) {
		this.isPartialShipment = isPartialShipment;
	}
}
