package com.cjh.maotai.swing.service.impl;

import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cjh.maotai.swing.beans.ReturnResultBean;
import com.cjh.maotai.swing.service.MaotaiService;
import com.cjh.maotai.swing.session.MaotaiSession;
import com.google.common.collect.Lists;

@Service
public class MaotaiServiceImpl implements MaotaiService {

	private final String userAgent = "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:61.0) Gecko/20100101 Firefox/61.0";

	private final String acceptEncoding = "gzip, deflate, br";

	private final String acceptLanguage = "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2";

	private final String connection = "keep-alive";
	
	@Autowired
	private MaotaiSession maotaiSession;

	private CookieStore getInitCookie() throws URISyntaxException, ClientProtocolException, IOException {
		List<Header> headerList = Lists.newArrayList();
		headerList.add(
				new BasicHeader(HttpHeaders.ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"));
		headerList.add(new BasicHeader(HttpHeaders.ACCEPT_ENCODING, acceptEncoding));
		headerList.add(new BasicHeader(HttpHeaders.ACCEPT_LANGUAGE, acceptLanguage));
		headerList.add(new BasicHeader(HttpHeaders.CONNECTION, connection));
		headerList.add(new BasicHeader(HttpHeaders.HOST, "www.emaotai.cn"));
		headerList.add(new BasicHeader("Upgrade-Insecure-Requests", "1"));
		headerList.add(new BasicHeader(HttpHeaders.USER_AGENT, userAgent));
		// 构造自定义的HttpClient对象
		CookieStore cookieStore = new BasicCookieStore();
		HttpClient httpClient = HttpClients.custom().setDefaultHeaders(headerList).setDefaultCookieStore(cookieStore)
				.build();
		String url = "https://www.emaotai.cn/smartsales-b2c-web-pc/login";
		URI uri = null;
		uri = new URIBuilder(url).build();
		HttpUriRequest httpUriRequest = RequestBuilder.get().setUri(uri).build();
		HttpClientContext httpClientContext = HttpClientContext.create();
		HttpResponse response = httpClient.execute(httpUriRequest, httpClientContext);
		if (response.getStatusLine().getStatusCode() == 200) {
			List<Cookie> cookies = cookieStore.getCookies();
			return cookieStore;
		} else {
			return null;
		}
	}

	private String getEncryptStr(String oStr) throws Exception {
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("JavaScript");
		engine.eval(new FileReader(ResourceUtils.getFile("classpath:MaotaiJs/encrypt.js")));
		Invocable inv = (Invocable) engine;
		return (String) inv.invokeFunction("getEncrypt", oStr);
	}

	@Override
	public ReturnResultBean login(String userName, String password) {
		ReturnResultBean resultBean = new ReturnResultBean();
		resultBean.setResultCode(-1);
		resultBean.setReturnMsg("登录失败");
		CookieStore cookieStore = null;
		try {
			cookieStore = getInitCookie();
		} catch (Exception e) {
			resultBean.setReturnMsg("登录失败" + e.getMessage());
			return resultBean;
		}
		if (cookieStore != null) {
			List<Header> headerList = Lists.newArrayList();
			headerList.add(new BasicHeader(HttpHeaders.ACCEPT, "application/json, text/javascript, */*; q=0.01"));
			headerList.add(new BasicHeader(HttpHeaders.ACCEPT_ENCODING, acceptEncoding));
			headerList.add(new BasicHeader(HttpHeaders.ACCEPT_LANGUAGE, acceptLanguage));
			headerList.add(new BasicHeader(HttpHeaders.CONNECTION, connection));
			headerList.add(new BasicHeader("appId", "1"));
			headerList.add(new BasicHeader("channelCode", "01"));
			headerList.add(new BasicHeader("channelId", "01"));
			headerList.add(new BasicHeader("Flag", "1"));
			headerList.add(new BasicHeader(HttpHeaders.HOST, "www.emaotai.cn"));
			headerList.add(new BasicHeader(HttpHeaders.REFERER, "https://www.emaotai.cn/smartsales-b2c-web-pc/login"));
			headerList.add(new BasicHeader(HttpHeaders.USER_AGENT, userAgent));
			headerList.add(new BasicHeader("tenantId", "1"));
			Date now = new Date();
			headerList.add(new BasicHeader("Timestamp", now.getTime() + ""));
			headerList.add(new BasicHeader("Sign", DigestUtils.md5Hex(now.getTime() + "")));
			// 构造自定义的HttpClient对象

			HttpClient httpClient = HttpClients.custom().setDefaultHeaders(headerList)
					.setDefaultCookieStore(cookieStore).build();
			String url = "https://www.emaotai.cn/huieryun-identity/api/v1/auth/XIANGLONG/user/b2cmember/auth?appCode=1&_t="
					+ now.getTime();
			URI uri = null;
			try {
				uri = new URIBuilder(url).build();
			} catch (URISyntaxException e) {
				resultBean.setReturnMsg("登录失败" + e.getMessage());
				return resultBean;
			}
			List<NameValuePair> params = Lists.newArrayList();
			params.add(new BasicNameValuePair("loginFlag", "1"));
			params.add(new BasicNameValuePair("loginSource", "2"));
			params.add(new BasicNameValuePair("loginType", "name"));
			params.add(new BasicNameValuePair("userCode", userName));
			try {
				params.add(new BasicNameValuePair("userPassword", getEncryptStr(password)));
				HttpUriRequest httpUriRequest;
				httpUriRequest = RequestBuilder.post().setEntity(new UrlEncodedFormEntity(params, "UTF-8")).setUri(uri)
						.build();
				HttpClientContext httpClientContext = HttpClientContext.create();
				HttpResponse response = httpClient.execute(httpUriRequest, httpClientContext);
				if (response.getStatusLine().getStatusCode() == 200) {
					HttpEntity entity = response.getEntity();
					if (entity != null) {
						String content = EntityUtils.toString(entity);
						JSONObject jsonObject = JSON.parseObject(content);
						Integer resultCode = jsonObject.getInteger("resultCode");
						if (resultCode != null && resultCode == 0) {
							resultBean.setResultCode(0);
							resultBean.setReturnObj(cookieStore);
							maotaiSession.setSession(userName, cookieStore);
							return resultBean;
						}
					}
				}
			} catch (Exception e1) {
				resultBean.setReturnMsg("登录失败" + e1.getMessage());
			}
		}
		return resultBean;
	}

	@Override
	public ReturnResultBean logout(String userName) {
		maotaiSession.removeSession(userName);
		ReturnResultBean resultBean = new ReturnResultBean();
		resultBean.setResultCode(0);
		resultBean.setReturnMsg("退出成功");
		return resultBean;
	}

}
