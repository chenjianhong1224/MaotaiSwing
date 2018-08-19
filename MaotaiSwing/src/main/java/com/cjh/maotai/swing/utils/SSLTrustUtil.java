package com.cjh.maotai.swing.utils;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;

import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;

public class SSLTrustUtil {

	public static HttpClientConnectionManager init() {
		try {
			SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, new TrustStrategy() {

				@Override
				public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
					// TODO Auto-generated method stub
					return true;
				}
			}).build();
			HostnameVerifier hv = new HostnameVerifier() {
				public boolean verify(String urlHostName, SSLSession session) {
					return true;
				}
			};
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, hv);
			Registry registry = RegistryBuilder.create().register("http", PlainConnectionSocketFactory.INSTANCE)
					.register("https", sslsf).build();
			// return new PoolingHttpClientConnectionManager(registry); 
			return new BasicHttpClientConnectionManager(registry);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}