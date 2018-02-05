package com.ml.meliproxy.reports.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ml.meliproxy.reports.api.ItemsDTO;
import com.ml.meliproxy.reports.api.ReportDTO;
import com.ml.meliproxy.reports.component.ReportsBean;

@RestController
@RequestMapping(value = "/paths")
public class PathsController {

	@Autowired
	private ReportsBean reportsBean;

	@RequestMapping(method = RequestMethod.GET)
	public ItemsDTO<ReportDTO> getAll() {
		List<ReportDTO> items = reportsBean.allPaths();
		return new ItemsDTO<>(items);
	}

	@RequestMapping(method = RequestMethod.GET, params = "path")
	public ItemsDTO<ReportDTO> getByIp(@RequestParam(value = "path") String path) {
		List<ReportDTO> items = reportsBean.getByPath(path);
		return new ItemsDTO<>(items);
	}
}
