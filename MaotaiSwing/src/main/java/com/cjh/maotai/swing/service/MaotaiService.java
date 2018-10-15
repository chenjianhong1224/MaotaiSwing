package com.cjh.maotai.swing.service;

import com.cjh.maotai.swing.beans.MaotaiSkuBean;
import com.cjh.maotai.swing.beans.ReturnResultBean;

public interface MaotaiService {
	
	public ReturnResultBean login(String userName, String password, String auth);
	
	public ReturnResultBean logout(String userName);
	
	public ReturnResultBean order(String auth, MaotaiSkuBean skuBean, String num, String purchaseWay);
	
	public ReturnResultBean getPrice(String url, String userName);
	
	public ReturnResultBean getOrderQCode(String auth);
}
