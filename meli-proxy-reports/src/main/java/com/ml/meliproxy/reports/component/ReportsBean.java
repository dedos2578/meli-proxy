package com.ml.meliproxy.reports.component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ml.meliproxy.persistence.repository.RequestRepository;
import com.ml.meliproxy.persistence.util.JsonUtil;
import com.ml.meliproxy.reports.api.ReportDTO;
import com.ml.meliproxy.reports.api.StatisticsDTO;

@Component
public class ReportsBean {

	@Autowired
	private RequestRepository repository;

	public List<ReportDTO> getByIp(String ip) {
		Comparator<ReportDTO> comparator = Comparator.comparing(ReportDTO::getPath)
				.thenComparing(ReportDTO::getHttpStatus);
		return this.repository.getByIp(ip).map(json -> JsonUtil.deserialize(json, ReportDTO.class)).sorted(comparator)
				.collect(Collectors.toList());
	}

	public List<ReportDTO> allIps() {
		Comparator<ReportDTO> comparator = Comparator.comparing(ReportDTO::getIp);
		return this.repository.allIps().map(json -> JsonUtil.deserialize(json, ReportDTO.class)).sorted(comparator)
				.collect(Collectors.toList());
	}

	public List<ReportDTO> getByPath(String path) {
		Comparator<ReportDTO> comparator = Comparator.comparing(ReportDTO::getIp)
				.thenComparing(ReportDTO::getHttpStatus);
		return this.repository.getByPath(path).map(json -> JsonUtil.deserialize(json, ReportDTO.class))
				.sorted(comparator).collect(Collectors.toList());
	}

	public List<ReportDTO> allPaths() {
		Comparator<ReportDTO> comparator = Comparator.comparing(ReportDTO::getPath);
		return this.repository.allPaths().map(json -> JsonUtil.deserialize(json, ReportDTO.class)).sorted(comparator)
				.collect(Collectors.toList());
	}

	public List<StatisticsDTO> statistics() {
		Comparator<StatisticsDTO> comparator = Comparator.comparing(StatisticsDTO::getPath)
				.thenComparing(StatisticsDTO::getHttpStatus);
		return this.repository.statistics().map(json -> JsonUtil.deserialize(json, StatisticsDTO.class))
				.sorted(comparator).collect(Collectors.toList());
	}
}
