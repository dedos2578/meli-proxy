package com.ml.meliproxy.reports.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ml.meliproxy.persistence.model.BlockedDoc;
import com.ml.meliproxy.persistence.model.ConfigDoc;
import com.ml.meliproxy.persistence.repository.BlockedRepository;
import com.ml.meliproxy.persistence.repository.ConfigRepository;
import com.ml.meliproxy.reports.api.BlockedDTO;
import com.ml.meliproxy.reports.api.ConfigDTO;

@Component
public class ConfigBean {

	@Autowired
	private ConfigRepository configRepository;

	@Autowired
	private BlockedRepository blockedRepository;

	public ConfigDTO save(ConfigDTO config) {
		ConfigDoc configDoc = configRepository.get();
		configDoc.setVersion(configDoc.getVersion() + 1);
		configDoc.setByIp(config.getByIp());
		configDoc.setByIpAndPath(config.getByIpAndPath());
		configDoc.setByPath(config.getByPath());
		configDoc.setDefaultLimit(config.getDefaultLimit());
		configRepository.save(configDoc);
		return config;
	}

	public ConfigDTO get() {
		ConfigDoc configDoc = configRepository.get();
		ConfigDTO dto = new ConfigDTO();
		dto.setByIp(configDoc.getByIp());
		dto.setByIpAndPath(configDoc.getByIpAndPath());
		dto.setByPath(configDoc.getByPath());
		dto.setDefaultLimit(configDoc.getDefaultLimit());
		return dto;
	}

	public BlockedDTO blocked() {
		BlockedDoc blockedDoc = blockedRepository.get();
		BlockedDTO dto = new BlockedDTO();
		dto.setByIp(blockedDoc.getByIp());
		dto.setByIpAndPath(blockedDoc.getByIpAndPath());
		dto.setByPath(blockedDoc.getByPath());
		return dto;
	}
}
