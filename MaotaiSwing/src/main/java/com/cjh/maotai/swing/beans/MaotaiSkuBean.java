package com.cjh.maotai.swing.beans;

import java.io.Serializable;

public class MaotaiSkuBean implements Serializable{
	 private String skuId;
	 private String itemId;
	 private String shopId;
	public String getSkuId() {
		return skuId;
	}
	public void setSkuId(String skuId) {
		this.skuId = skuId;
	}
	public String getItemId() {
		return itemId;
	}
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	public String getShopId() {
		return shopId;
	}
	public void setShopId(String shopId) {
		this.shopId = shopId;
	}
}
