package com.ml.meliproxy.reports.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ml.meliproxy.reports.api.ItemsDTO;
import com.ml.meliproxy.reports.api.StatisticsDTO;
import com.ml.meliproxy.reports.component.ReportsBean;

@RestController
@RequestMapping(value = "/statistics")
public class StatisticsController {

	@Autowired
	private ReportsBean reportsBean;

	@RequestMapping(method = RequestMethod.GET)
	public ItemsDTO<StatisticsDTO> getAll() {
		List<StatisticsDTO> items = reportsBean.statistics();
		return new ItemsDTO<>(items);
	}
}
