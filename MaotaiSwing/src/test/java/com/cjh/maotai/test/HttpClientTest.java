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
// 加上就跑不了，这个问题待查
// @RunWith(SpringRunner.class)
public class HttpClientTest implements Runnable {

	String testUrl = "https://i.emaotai.cn/smartsales-trade-application/api/v1/smartsales/trade/mall/link/list";

	String getMemberUrl = "https://i.emaotai.cn/yundt-application-trade-core/api/v1/yundt/trade/member/account/detail/get";

	static PoolingHttpClientConnectionManager pm = null;

	static AtomicInteger requestCount = new AtomicInteger(0);

	static AtomicBoolean finishFlag = new AtomicBoolean(true);

	String faceVeriFyUrl = "https://www.cmaotai.com/index.html?faceverify=";

	// final String proxyIp = "89.208.212.2";
	//
	// final int proxyPort = 80;

	private boolean model = true;

	String auth = "eyJhbGciOiJIUzUxMiJ9.eyJqdGkiOiI0N2Q4MTk3YS01Y2Q2LTQyZmUtYTkzZi1mYzUwYTQ2YTdhNjQiLCJzdWIiOiJrZXkmX18mWElBTkdMT05HJl9fJjI5Nzg2OTQmX18mY2h3X2sxJl9fJjAmX18mYjJjbWVtYmVyJl9fJnd4Jl9fJjEmX18mMCJ9.9UjrVPxiV4P1cp5MAJsbqSb05aDi-qMfOv-it4m6LDPak3r2zXB708aAvQKLpnFAZbCMKu0xTTGwxhaOxcej-A";

	static long startTime;

	static long endTime;

	private PoolingHttpClientConnectionManager getPoolingHttpClientConnectionManager() {
		synchronized (this.getClass()) {
			if (pm == null) {
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
					pm = new PoolingHttpClientConnectionManager(registry);
				} catch (Exception e) {
					e.printStackTrace();
					return pm;
				}
				// 整个连接池的最大连接数
				pm.setMaxTotal(32);
				// 单个路由默认连接数
				pm.setDefaultMaxPerRoute(2);
				HttpHost httpHost = new HttpHost("i.emaotai.cn", 443);
				// 单个路由的最大连接数
				pm.setMaxPerRoute(new HttpRoute(httpHost), 4);
			}
		}
		return pm;
	}

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
						System.out.println("https://www.cmaotai.com/index.html?faceverify=" + jsonObject.getString("data"));
					}
					EntityUtils.consume(entity);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testThreadRequest() {
		List<Thread> threadList = Lists.newArrayList();
		for (int i = 0; i < 32; i++) {
			Thread t = new Thread(this);
			threadList.add(t);
			t.start();
		}
		for (Thread t : threadList) {
			try {
				t.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		double excTime = (double) (endTime - startTime) / 1000;
		System.out.println("在" + excTime + "内共请求了" + requestCount.get() + "次");
	}

	final String proxyIp = "182.87.240.109";

	final int proxyPort = 808;

	@Test
	public void testProxy() {
		Date now = new Date();
		URI uri = null;
		try {
			uri = new URIBuilder(getMemberUrl + "?appCode=1&_t=" + now.getTime()).build();
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return;
		}
		do {
			// 设置代理IP、端口、协议（请分别替换）
			HttpHost proxy = new HttpHost(proxyIp, proxyPort, "http");
			// 把代理设置到请求配置
			RequestConfig defaultRequestConfig = RequestConfig.custom().setProxy(proxy).build();
			HttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(defaultRequestConfig)
					.setDefaultHeaders(getRequestHttpHead(now)).build();
			HttpUriRequest httpUriRequest = RequestBuilder.get().setUri(uri).build();
			HttpClientContext httpClientContext = HttpClientContext.create();
			try {
				long startTime = System.currentTimeMillis();
				int j = requestCount.incrementAndGet();
				if (j == 1) {
					this.startTime = startTime;
				}
				HttpResponse response = httpClient.execute(httpUriRequest, httpClientContext);
				if (response.getStatusLine().getStatusCode() == 200) {
					HttpEntity entity = response.getEntity();
					if (entity != null) {
						String content = EntityUtils.toString(entity);
						JSONObject jsonObject = JSON.parseObject(content);
						if ((jsonObject.getInteger("resultCode") == 0)
								&& jsonObject.getString("resultMsg").equals("success")) {
							JSONObject dataObject = jsonObject.getJSONObject("data");
							System.out.println(
									"[" + Thread.currentThread().getId() + "] memberId=" + dataObject.getString("id"));
						}
						EntityUtils.consume(entity);
					}
				}
			} catch (NoHttpResponseException e1) {
				if (!finishFlag.getAndSet(true)) {
					this.endTime = System.currentTimeMillis();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} while (!finishFlag.get());
	}

	@Test
	public void testLongConnect() {
		Date now = new Date();
		URI uri = null;
		try {
			uri = new URIBuilder(getMemberUrl + "?appCode=1&_t=" + now.getTime()).build();
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return;
		}
		ConnectionKeepAliveStrategy myStrategy = new ConnectionKeepAliveStrategy() {
			@Override
			public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
				HeaderElementIterator it = new BasicHeaderElementIterator(
						response.headerIterator(HTTP.CONN_KEEP_ALIVE));
				while (it.hasNext()) {
					HeaderElement he = it.nextElement();
					String param = he.getName();
					String value = he.getValue();
					if (value != null && param.equalsIgnoreCase("timeout")) {
						return Long.parseLong(value) * 1000;
					}
				}
				return 60 * 1000;// 如果没有约定，则默认定义时长为60s
			}
		};
		do {
			HttpClientConnectionManager clientConnectionManager = getPoolingHttpClientConnectionManager();
			HttpClient httpClient = HttpClients.custom().setConnectionManager(clientConnectionManager)
					.setKeepAliveStrategy(myStrategy).setDefaultHeaders(getRequestHttpHead(now))
					.evictExpiredConnections().evictIdleConnections(5, TimeUnit.SECONDS).build();
			HttpUriRequest httpUriRequest = RequestBuilder.get().setUri(uri).build();
			HttpClientContext httpClientContext = HttpClientContext.create();
			try {
				long startTime = System.currentTimeMillis();
				int j = requestCount.incrementAndGet();
				if (j == 1) {
					this.startTime = startTime;
				}
				HttpResponse response = httpClient.execute(httpUriRequest, httpClientContext);
				if (response.getStatusLine().getStatusCode() == 200) {
					HttpEntity entity = response.getEntity();
					if (entity != null) {
						String content = EntityUtils.toString(entity);
						JSONObject jsonObject = JSON.parseObject(content);
						if ((jsonObject.getInteger("resultCode") == 0)
								&& jsonObject.getString("resultMsg").equals("success")) {
							JSONObject dataObject = jsonObject.getJSONObject("data");
							System.out.println(
									"[" + Thread.currentThread().getId() + "] memberId=" + dataObject.getString("id"));
						}
						EntityUtils.consume(entity);
					}
				}
			} catch (NoHttpResponseException e1) {
				if (!finishFlag.getAndSet(true)) {
					this.endTime = System.currentTimeMillis();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} while (!finishFlag.get());
	}

	@Override
	public void run() {
		if (model) {
			testLongConnect();
		} else {
			testProxy();
		}
	}
}
