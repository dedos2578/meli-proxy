package com.ml.meliproxy.reports.api;

public class StatisticsDTO {
	private Long count;
	private Double proxyTimeAvg;
	private Double serviceTimeAvg;
	private Double diffAvg;
	private String path;
	private Long httpStatus;

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public Double getProxyTimeAvg() {
		return proxyTimeAvg;
	}

	public void setProxyTimeAvg(Double proxyTimeAvg) {
		this.proxyTimeAvg = proxyTimeAvg;
	}

	public Double getServiceTimeAvg() {
		return serviceTimeAvg;
	}

	public void setServiceTimeAvg(Double serviceTimeAvg) {
		this.serviceTimeAvg = serviceTimeAvg;
	}

	public Double getDiffAvg() {
		return diffAvg;
	}

	public void setDiffAvg(Double diffAvg) {
		this.diffAvg = diffAvg;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Long getHttpStatus() {
		return httpStatus;
	}

	public void setHttpStatus(Long httpStatus) {
		this.httpStatus = httpStatus;
	}
}
