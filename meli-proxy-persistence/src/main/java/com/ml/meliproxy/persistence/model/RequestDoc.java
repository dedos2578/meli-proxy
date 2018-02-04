package com.ml.meliproxy.persistence.model;

import java.util.Date;

public class RequestDoc {
	private String path;
	private String fullPath;
	private String ip;
	private String date;
	private Date datetime;
	private String httpMethod;
	private Integer httpStatus;
	private Long serviceTime;
	private Long proxyTime;
	private boolean blocked;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public Date getDatetime() {
		return datetime;
	}

	public void setDatetime(Date datetime) {
		this.datetime = datetime;
	}

	public String getHttpMethod() {
		return httpMethod;
	}

	public void setHttpMethod(String httpMethod) {
		this.httpMethod = httpMethod;
	}

	public Integer getHttpStatus() {
		return httpStatus;
	}

	public void setHttpStatus(Integer httpStatus) {
		this.httpStatus = httpStatus;
	}

	public boolean isBlocked() {
		return blocked;
	}

	public void setBlocked(boolean blocked) {
		this.blocked = blocked;
	}

	public Long getServiceTime() {
		return serviceTime;
	}

	public void setServiceTime(Long serviceTime) {
		this.serviceTime = serviceTime;
	}

	public Long getProxyTime() {
		return proxyTime;
	}

	public void setProxyTime(Long proxyTime) {
		this.proxyTime = proxyTime;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getFullPath() {
		return fullPath;
	}

	public void setFullPath(String fullPath) {
		this.fullPath = fullPath;
	}
}
