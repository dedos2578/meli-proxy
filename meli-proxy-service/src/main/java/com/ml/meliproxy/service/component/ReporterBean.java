package com.ml.meliproxy.service.component;

import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ml.meliproxy.persistence.model.RequestDoc;
import com.ml.meliproxy.persistence.repository.RequestRepository;

@Component
public class ReporterBean {
	private static final String DATE_PATTERN = "yyyy-MM-dd";

	@Autowired
	private RequestRepository requestRepository;

	public void report(String ip, String path, String fullPath, String httpMethod, Integer httpStatus, boolean blocked,
			Long serviceStart, Long proxyStart) {
		Date now = new Date();
		long currentTime = now.getTime();

		RequestDoc entity = new RequestDoc();
		entity.setBlocked(blocked);
		entity.setDate(DateFormatUtils.format(now, DATE_PATTERN));
		entity.setDatetime(now);
		entity.setHttpMethod(httpMethod);
		entity.setHttpStatus(httpStatus);
		entity.setIp(ip);
		entity.setProxyTime(currentTime - proxyStart);
		entity.setServiceTime(serviceStart == null ? 0L : currentTime - serviceStart);
		entity.setPath(path);
		entity.setFullPath(fullPath);
		requestRepository.save(entity);
	}
}
