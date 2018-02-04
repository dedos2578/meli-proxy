package com.ml.meliproxy.persistence.model;

import java.util.Map;

public class ConfigDoc {
	private Map<String, Long> byIp;
	private Map<String, Long> byPath;
	private Map<String, Map<String, Long>> byIpAndPath;
	private Long defaultLimit;
	private Long version;

	public Map<String, Long> getByIp() {
		return byIp;
	}

	public void setByIp(Map<String, Long> byIp) {
		this.byIp = byIp;
	}

	public Map<String, Long> getByPath() {
		return byPath;
	}

	public void setByPath(Map<String, Long> byPath) {
		this.byPath = byPath;
	}

	public Map<String, Map<String, Long>> getByIpAndPath() {
		return byIpAndPath;
	}

	public void setByIpAndPath(Map<String, Map<String, Long>> byIpAndPath) {
		this.byIpAndPath = byIpAndPath;
	}

	public Long getDefaultLimit() {
		return defaultLimit;
	}

	public void setDefaultLimit(Long defaultLimit) {
		this.defaultLimit = defaultLimit;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}
}
