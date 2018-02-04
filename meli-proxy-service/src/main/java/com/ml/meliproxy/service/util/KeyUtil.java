package com.ml.meliproxy.service.util;

public abstract class KeyUtil {
	public static String combine(String ip, String url) {
		return ip + "::" + url;
	}
}
