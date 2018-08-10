package com.cjh.maotai.swing.service;

import com.cjh.maotai.swing.beans.ReturnResultBean;

public interface MaotaiService {
	
	public ReturnResultBean login(String userName, String password);
	
	public ReturnResultBean logout(String userName);
}
