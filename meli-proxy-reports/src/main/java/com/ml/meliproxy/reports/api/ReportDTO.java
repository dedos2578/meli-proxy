package com.ml.meliproxy.reports.api;

public class ReportDTO {
	private String ip;
	private String path;
	private Long httpStatus;
	private Long count;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
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

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}
}
