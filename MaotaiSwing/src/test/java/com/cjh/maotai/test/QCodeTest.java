package com.cjh.maotai.test;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cjh.maotai.swing.service.impl.MaotaiServiceImpl;
import com.cjh.maotai.swing.utils.SSLTrustUtil;
import com.google.common.collect.Lists;

@SpringBootTest
public class QCodeTest {

	String faceVeriFyUrl = "https://www.cmaotai.com/index.html?faceverify=";

	String auth = "eyJhbGciOiJIUzUxMiJ9.eyJqdGkiOiJkNDg0ODNiMC02NWE5LTQ0ZGEtOTU1My0wOWI3ZjE3NWM2ODQiLCJzdWIiOiJrZXkmX18mWElBTkdMT05HJl9fJjI5Nzg2OTQmX18mY2h3X2sxJl9fJjAmX18mYjJjbWVtYmVyJl9fJnd4Jl9fJjEmX18mMCJ9.NK-9Zwpje3oLA31AJ8Mqp-0B_LS24qpMkc7HrCQa3Uqb7E36zwoEJ0IO5Zzv3F2KHSXtYe9c4PaaE7lud-u3nw";

	private List<Header> getRequestHttpHead(Date now) {
		List<Header> headerList = Lists.newArrayList();
		headerList.add(new BasicHeader(HttpHeaders.ACCEPT, "application/json,text/javascript,*/*;q=0.01"));
		headerList.add(new BasicHeader(HttpHeaders.ACCEPT_ENCODING, MaotaiServiceImpl.acceptEncoding));
		headerList.add(new BasicHeader(HttpHeaders.ACCEPT_LANGUAGE, MaotaiServiceImpl.acceptLanguage));
		headerList.add(new BasicHeader(HttpHeaders.CONNECTION, MaotaiServiceImpl.connection));
		headerList.add(new BasicHeader("appId", "1"));
		headerList.add(new BasicHeader("channelCode", "01"));
		headerList.add(new BasicHeader("channelId", "01"));
		headerList.add(new BasicHeader("Flag", "1"));
		headerList.add(new BasicHeader(HttpHeaders.HOST, "i.emaotai.cn"));
		headerList.add(new BasicHeader("Origin", "https://www.emaotai.cn"));
		headerList.add(new BasicHeader("Referer", "https://www.emaotai.cn/smartsales-b2c-web-pc/login"));
		headerList.add(new BasicHeader("tenantType", "a1"));
		headerList.add(new BasicHeader("tenantId", "1"));
		headerList.add(new BasicHeader("Timestamp", now.getTime() + ""));
		headerList.add(new BasicHeader(HttpHeaders.USER_AGENT, MaotaiServiceImpl.userAgent));
		headerList.add(new BasicHeader("auth", auth));
		return headerList;
	}

	@Test
	public void testGetVerifyCode() {
		Date now = new Date();
		URI uri = null;
		try {
			uri = new URIBuilder("https://i.emaotai.cn/huieryun-identity/api/v1/authant/faceverify/compress"
					+ "?appCode=1&_t=" + now.getTime() + "&faceverify=scan&mtBizId=6&auth=" + auth).build();
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return;
		}
		HttpClient httpClient = HttpClients.custom().setDefaultHeaders(getRequestHttpHead(now)).build();
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
						System.out.println(
								"https://www.cmaotai.com/index.html?faceverify=" + jsonObject.getString("data"));
					}
					EntityUtils.consume(entity);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testFace() {
		URI uri = null;
		try {
			uri = new URIBuilder("https://cn-hangzhou-mgs-gw.cloud.alipay.com/mgw.htm").build();
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return;
		}
	}
}
