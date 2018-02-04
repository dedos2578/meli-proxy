package com.ml.meliproxy.persistence.model;

import java.util.Set;

public class BlockedDoc {
	private Set<String> byIp;
	private Set<String> byPath;
	private Set<String> byIpAndPath;

	public Set<String> getByIp() {
		return byIp;
	}

	public void setByIp(Set<String> byIp) {
		this.byIp = byIp;
	}

	public Set<String> getByPath() {
		return byPath;
	}

	public void setByPath(Set<String> byPath) {
		this.byPath = byPath;
	}

	public Set<String> getByIpAndPath() {
		return byIpAndPath;
	}

	public void setByIpAndPath(Set<String> byIpAndPath) {
		this.byIpAndPath = byIpAndPath;
	}
}
