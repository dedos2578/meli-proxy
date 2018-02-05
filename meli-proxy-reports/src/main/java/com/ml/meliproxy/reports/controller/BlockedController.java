package com.ml.meliproxy.reports.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ml.meliproxy.reports.api.BlockedDTO;
import com.ml.meliproxy.reports.component.ConfigBean;

@RestController
@RequestMapping(value = "/blocked")
public class BlockedController {

	@Autowired
	private ConfigBean configBean;

	@RequestMapping(method = RequestMethod.GET)
	public BlockedDTO get() {
		return configBean.blocked();
	}
}
