package com.cjh.maotai.swing.service.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.message.StringFormattedMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cjh.maotai.swing.beans.MaotaiOrderBase;
import com.cjh.maotai.swing.beans.MaotaiOrderBean;
import com.cjh.maotai.swing.beans.MaotaiOrderItem;
import com.cjh.maotai.swing.beans.MaotaiOrderSend;
import com.cjh.maotai.swing.beans.MaotaiSkuBean;
import com.cjh.maotai.swing.beans.ReturnResultBean;
import com.cjh.maotai.swing.service.MaotaiService;
import com.cjh.maotai.swing.session.MaotaiSession;
import com.cjh.maotai.swing.utils.MaotaiUrlParseUtil;
import com.cjh.maotai.swing.utils.SSLTrustUtil;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;

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
		HttpClientConnectionManager clientConnectionManager = SSLTrustUtil.init();
		HttpClient httpClient = HttpClients.custom().setConnectionManager(clientConnectionManager)
				.setDefaultHeaders(headerList).setDefaultCookieStore(cookieStore).build();
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
		InputStream inputStream = (InputStream) Resources.getResource("MaotaiJs/encrypt.js").getContent();
		engine.eval(new InputStreamReader(inputStream));
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
			HttpClientConnectionManager clientConnectionManager = SSLTrustUtil.init();
			HttpClient httpClient = HttpClients.custom().setConnectionManager(clientConnectionManager)
					.setDefaultHeaders(headerList).setDefaultCookieStore(cookieStore).build();
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
							String auth = maotaiSession.getValidAuth(userName);
							ReturnResultBean addrResBean = getAddress(auth);
							if (addrResBean.getResultCode() == 0) {
								String[] tmp = ((String) addrResBean.getReturnObj()).split("\\|");
								maotaiSession.setAddress(tmp[1]);
								maotaiSession.setAddressId(tmp[0]);
								return resultBean;
							} else {
								maotaiSession.removeSession(userName);
								return addrResBean;
							}
						}
					}
				}
			} catch (Exception e1) {
				resultBean.setResultCode(-1);
				resultBean.setReturnMsg("登录失败" + e1.getMessage());
			}
		}
		return resultBean;
	}

	private ReturnResultBean getAddress(String auth) {
		ReturnResultBean resultBean = new ReturnResultBean();
		resultBean.setResultCode(-1);
		resultBean.setReturnMsg("获取收货地址失败");
		List<Header> headerList = Lists.newArrayList();
		headerList.add(new BasicHeader(HttpHeaders.ACCEPT, "application/json, text/javascript, */*; q=0.01"));
		headerList.add(new BasicHeader(HttpHeaders.ACCEPT_ENCODING, acceptEncoding));
		headerList.add(new BasicHeader(HttpHeaders.ACCEPT_LANGUAGE, acceptLanguage));
		headerList.add(new BasicHeader(HttpHeaders.CONNECTION, connection));
		headerList.add(new BasicHeader("appId", "1"));
		headerList.add(new BasicHeader("auth", auth));
		headerList.add(new BasicHeader("channelCode", "01"));
		headerList.add(new BasicHeader("channelId", "01"));
		headerList.add(new BasicHeader("Flag", "1"));
		headerList.add(new BasicHeader(HttpHeaders.HOST, "i.emaotai.cn"));
		headerList.add(new BasicHeader("Origin", "https://www.emaotai.cn"));
		headerList.add(
				new BasicHeader(HttpHeaders.REFERER, "https://www.emaotai.cn/smartsales-b2c-web-pc/usercenter/adress"));
		Date now = new Date();
		String encodeStr = DigestUtils.md5Hex(auth + now.getTime() + "");
		headerList.add(new BasicHeader("Sign", encodeStr));
		headerList.add(new BasicHeader("tenantId", "1"));
		headerList.add(new BasicHeader("terminalType", "a1"));
		headerList.add(new BasicHeader("Timestamp", now.getTime() + ""));
		headerList.add(new BasicHeader(HttpHeaders.USER_AGENT, userAgent));
		HttpClientConnectionManager clientConnectionManager = SSLTrustUtil.init();
		HttpClient httpClient = HttpClients.custom().setConnectionManager(clientConnectionManager)
				.setDefaultHeaders(headerList).build();
		String url = "https://i.emaotai.cn/yundt-application-trade-core/api/v1/yundt/trade/member/address/list?appCode=1&_t="
				+ now.getTime();
		URI uri = null;
		try {
			uri = new URIBuilder(url).build();
		} catch (URISyntaxException e) {
			resultBean.setResultCode(-1);
			resultBean.setReturnMsg("获取收货地址失败 " + e.getMessage());
			return resultBean;
		}
		HttpUriRequest httpUriRequest = RequestBuilder.get().setUri(uri).build();
		HttpClientContext httpClientContext = HttpClientContext.create();
		try {
			HttpResponse response = httpClient.execute(httpUriRequest, httpClientContext);
			if (response.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					String content = EntityUtils.toString(entity);
					JSONObject jsonObject = JSON.parseObject(content);
					if ((jsonObject.getInteger("resultCode") == 0)
							&& jsonObject.getString("resultMsg").equals("success")) {
						JSONArray jsonArray = jsonObject.getJSONArray("data");
						if (jsonArray.size() > 0) {
							JSONObject addrObj = (JSONObject) jsonArray.get(0);
							resultBean.setReturnObj(addrObj.getString("id") + "|" + addrObj.getString("address"));
							resultBean.setResultCode(0);
						}
					}
				}
			}
		} catch (Exception e) {
			resultBean.setReturnMsg("获取收货地址失败 " + e.getMessage());
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

	@Override
	public ReturnResultBean order(String auth, MaotaiSkuBean skuBean, String num, String purchaseWay) {
		ReturnResultBean resultBean = new ReturnResultBean();
		resultBean.setResultCode(0);
		List<MaotaiOrderBean> maotaiOrderBeans = Lists.newArrayList();
		MaotaiOrderBean maotaiOrderBean = new MaotaiOrderBean();
		MaotaiOrderBase maotaiOrderBase = new MaotaiOrderBase();
		maotaiOrderBase.setRemark("");
		maotaiOrderBase.setShopId(skuBean.getShopId());
		maotaiOrderBean.setMaotaiOrderBase(maotaiOrderBase);
		MaotaiOrderSend maotaiOrderSend = new MaotaiOrderSend();
		maotaiOrderSend.setDeliveryId("4");
		maotaiOrderSend.setAddressId(maotaiSession.getAddressId());
		maotaiOrderBean.setMaotaiOrderSend(maotaiOrderSend);
		MaotaiOrderItem maotaiOrderItem = new MaotaiOrderItem();
		maotaiOrderItem.setIsPartialShipment("0");
		maotaiOrderItem.setItemId(skuBean.getItemId());
		maotaiOrderItem.setItemNum(num);
		maotaiOrderItem.setItemPrice(skuBean.getSellPrice());
		maotaiOrderItem.setShopId(skuBean.getShopId());
		maotaiOrderItem.setSkuId(skuBean.getSkuId());
		List<MaotaiOrderItem> maotaiOrderItems = Lists.newArrayList();
		maotaiOrderItems.add(maotaiOrderItem);
		maotaiOrderBean.setMaotaiOrderItems(maotaiOrderItems);
		maotaiOrderBeans.add(maotaiOrderBean);
		maotaiOrderBean.setPurchaseWay(Integer.valueOf(purchaseWay));
		maotaiOrderBean.setVoucherId(0);
		String orderBean = JSON.toJSONString(maotaiOrderBeans);
		// 构造自定义Header信息
		List<Header> headerList = Lists.newArrayList();
		headerList.add(new BasicHeader(HttpHeaders.ACCEPT, "application/json, text/javascript, */*; q=0.01"));
		headerList.add(new BasicHeader(HttpHeaders.ACCEPT_ENCODING, acceptEncoding));
		headerList.add(new BasicHeader(HttpHeaders.ACCEPT_LANGUAGE, acceptLanguage));
		headerList.add(new BasicHeader("appId", "1"));
		headerList.add(new BasicHeader("auth", auth));
		headerList.add(new BasicHeader("channelCode", "01"));
		headerList.add(new BasicHeader("channelId", "01"));
		headerList.add(new BasicHeader(HttpHeaders.CONNECTION, connection));
		headerList.add(new BasicHeader("Flag", "1"));
		headerList.add(new BasicHeader(HttpHeaders.HOST, "i.emaotai.cn"));
		headerList.add(new BasicHeader(HttpHeaders.REFERER, "https://www.emaotai.cn/smartsales-b2c-web-pc/deal"));
		Date now = new Date();
		String encodeStr = DigestUtils.md5Hex(auth + now.getTime() + "");
		headerList.add(new BasicHeader("Sign", encodeStr));
		headerList.add(new BasicHeader("tenantId", "1"));
		headerList.add(new BasicHeader("terminalType", "a1"));
		headerList.add(new BasicHeader("Timestamp", now.getTime() + ""));
		headerList.add(new BasicHeader(HttpHeaders.USER_AGENT, userAgent));
		HttpClientConnectionManager clientConnectionManager = SSLTrustUtil.init();
		HttpClient httpClient = HttpClients.custom().setConnectionManager(clientConnectionManager)
				.setDefaultHeaders(headerList).build();
		String url = "https://i.emaotai.cn/yundt-application-trade-core/api/v1/yundt/trade/order/submit?appCode=1&_t="
				+ now.getTime();
		URI uri = null;
		try {
			uri = new URIBuilder(url).build();
		} catch (URISyntaxException e) {
			resultBean.setResultCode(-1);
			resultBean.setReturnMsg("下单失败 " + e.getMessage());
			return resultBean;
		}
		List<NameValuePair> params = Lists.newArrayList();
		params.add(new BasicNameValuePair("orders", orderBean));
		params.add(new BasicNameValuePair("consumePoint", "0"));
		HttpUriRequest httpUriRequest;
		try {
			httpUriRequest = RequestBuilder.post().setEntity(new UrlEncodedFormEntity(params, "UTF-8")).setUri(uri)
					.build();
			RequestConfig defaultRequestConfig = RequestConfig.custom().setSocketTimeout(5000).setConnectTimeout(5000)
					.setConnectionRequestTimeout(5000).build();
			HttpClientContext httpClientContext = HttpClientContext.create();
			httpClientContext.setRequestConfig(defaultRequestConfig);
			HttpResponse response = httpClient.execute(httpUriRequest, httpClientContext);
			String content = "";
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				content = EntityUtils.toString(entity);
				if (response.getStatusLine().getStatusCode() == 200) {
					JSONObject jsonObject = JSON.parseObject(content);
					if ((jsonObject.getInteger("resultCode") == 0)
							&& jsonObject.getString("resultMsg").equals("success")) {
						return resultBean;
					}
				}
			}
			resultBean.setResultCode(-1);
			resultBean.setReturnMsg(
					String.format("下单失败, http 响应码[%d], 响应报文[%s]", response.getStatusLine().getStatusCode(), content));
		} catch (Exception e) {
			resultBean.setResultCode(-1);
			resultBean.setReturnMsg("下单失败 " + e.getMessage());
		}
		return resultBean;
	}

	@Override
	public ReturnResultBean getPrice(String url, String userName) {
		ReturnResultBean resultBean = new ReturnResultBean();
		resultBean.setResultCode(-1);
		resultBean.setReturnMsg("获取价格失败");
		ReturnResultBean parseResult = MaotaiUrlParseUtil.parseSkuUrl(url);
		if (parseResult.getResultCode() != 0) {
			return resultBean;
		}
		List<Header> headerList = Lists.newArrayList();
		headerList.add(new BasicHeader(HttpHeaders.ACCEPT, "application/json, text/javascript, */*; q=0.01"));
		headerList.add(new BasicHeader(HttpHeaders.ACCEPT_ENCODING, acceptEncoding));
		headerList.add(new BasicHeader(HttpHeaders.ACCEPT_LANGUAGE, acceptLanguage));
		headerList.add(new BasicHeader(HttpHeaders.CONNECTION, connection));
		headerList.add(new BasicHeader("appId", "1"));
		headerList.add(new BasicHeader("channelCode", "01"));
		headerList.add(new BasicHeader("channelId", "01"));
		headerList.add(new BasicHeader("Flag", "1"));
		headerList.add(new BasicHeader(HttpHeaders.HOST, "i.emaotai.cn"));
		headerList.add(new BasicHeader("Origin", "https://www.emaotai.cn"));
		headerList.add(new BasicHeader(HttpHeaders.REFERER, url));
		headerList.add(new BasicHeader(HttpHeaders.USER_AGENT, userAgent));
		headerList.add(new BasicHeader("tenantId", "1"));
		headerList.add(new BasicHeader("terminalType", "a1"));
		Date now = new Date();
		headerList.add(new BasicHeader("Timestamp", now.getTime() + ""));
		headerList.add(new BasicHeader("Sign", DigestUtils.md5Hex(now.getTime() + "")));
		// 构造自定义的HttpClient对象

		CookieStore cookieStore = MaotaiSession.getSession(userName);
		HttpClientConnectionManager clientConnectionManager = SSLTrustUtil.init();
		HttpClient httpClient = HttpClients.custom().setConnectionManager(clientConnectionManager)
				.setDefaultHeaders(headerList).setDefaultCookieStore(cookieStore).build();
		MaotaiSkuBean skuBean = (MaotaiSkuBean) parseResult.getReturnObj();
		String requestUrl = "https://i.emaotai.cn/yundt-application-trade-core/api/v1/yundt/trade/item/sku/get?shopId="
				+ skuBean.getShopId() + "&itemId=" + skuBean.getItemId() + "&skuId=" + skuBean.getSkuId()
				+ "&appCode=1&_t=" + now.getTime();
		URI uri = null;
		try {
			uri = new URIBuilder(requestUrl).build();
		} catch (URISyntaxException e) {
			resultBean.setReturnMsg("获取价格失败" + e.getMessage());
			return resultBean;
		}
		HttpUriRequest httpUriRequest = RequestBuilder.get().setUri(uri).build();
		HttpClientContext httpClientContext = HttpClientContext.create();
		HttpResponse response;
		try {
			response = httpClient.execute(httpUriRequest, httpClientContext);
			if (response.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					String content = EntityUtils.toString(entity);
					JSONObject jsonObject = JSON.parseObject(content);
					if ((jsonObject.getInteger("resultCode") == 0)
							&& jsonObject.getString("resultMsg").equals("success")) {
						JSONObject data = jsonObject.getJSONObject("data");
						Double sellPrice = data.getDouble("sellPrice");
						resultBean.setReturnObj(sellPrice);
						resultBean.setResultCode(0);
						return resultBean;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			resultBean.setReturnMsg(resultBean.getReturnMsg() + " " + e.getMessage());
		}
		return resultBean;
	}

}
