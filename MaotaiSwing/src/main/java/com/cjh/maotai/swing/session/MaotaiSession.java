package com.cjh.maotai.swing.session;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;

@Component
@Scope("singleton")
public class MaotaiSession {

	private static Map<String, CookieStore> sessionMap = Maps.newHashMap();

	public void setSession(String userName, CookieStore cookieStore) {
		synchronized (this.getClass()) {
			sessionMap.put(userName, cookieStore);
		}
	}

	public void removeSession(String userName) {
		synchronized (this.getClass()) {
			sessionMap.remove(userName);
		}
	}

	public static CookieStore getSession(String userName) {
		CookieStore cookieStore = sessionMap.get(userName);
		return cookieStore;
	}

	public static String getValidAuth(String userName) {
		CookieStore cookieStore = sessionMap.get(userName);
		if (cookieStore != null) {
			List<Cookie> cookies = cookieStore.getCookies();
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("sso.login.account.auth")) {
					Date now = new Date();
					if (cookie.getExpiryDate().getTime() > now.getTime()) {
						return cookie.getValue();
					}
				}
			}
		}
		return null;
	}

	public static boolean isLogined() {
		for (Iterator<Map.Entry<String, CookieStore>> it = sessionMap.entrySet().iterator(); it.hasNext();) {
			CookieStore cookieStore = it.next().getValue();
			if (cookieStore != null) {
				List<Cookie> cookies = cookieStore.getCookies();
				for (Cookie cookie : cookies) {
					if (cookie.getName().equals("sso.login.account.auth")) {
						Date now = new Date();
						if (cookie.getExpiryDate().getTime() > now.getTime()) {
							return true;
						} else {
							it.remove();
						}
					}
				}
			}
		}
		return false;
	}
}
