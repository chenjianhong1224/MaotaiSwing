package com.cjh.maotai.swing.config;

import java.util.List;

public class ProxyConfig {
	private static ProxyConfig instance;

	private ProxyConfig() {
	}

	public static synchronized ProxyConfig getInstance() {
		if (instance == null) {
			instance = new ProxyConfig();
		}
		return instance;
	}
	public synchronized List<String> getAddress() {
		return address;
	}

	public synchronized void setAddress(List<String> address) {
		this.address = address;
	}
	public boolean isUseFlag() {
		return useFlag;
	}

	public void setUseFlag(boolean useFlag) {
		this.useFlag = useFlag;
	}
	private List<String> address;
	
	private boolean useFlag;
}
