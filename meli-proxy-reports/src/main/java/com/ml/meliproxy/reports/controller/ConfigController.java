package com.ml.meliproxy.reports.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ml.meliproxy.reports.api.ConfigDTO;
import com.ml.meliproxy.reports.component.ConfigBean;

@RestController
@RequestMapping(value = "/configs")
public class ConfigController {

	@Autowired
	private ConfigBean configBean;

	@RequestMapping(method = RequestMethod.GET)
	public ConfigDTO get() {
		return configBean.get();
	}

	@RequestMapping(method = RequestMethod.POST)
	public ConfigDTO save(@RequestBody ConfigDTO config) {
		return configBean.save(config);
	}
}
