package com.ml.meliproxy.service.component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ml.meliproxy.persistence.model.ConfigDoc;
import com.ml.meliproxy.persistence.repository.ConfigRepository;
import com.ml.meliproxy.persistence.repository.CounterRepository;
import com.ml.meliproxy.service.component.AccessControlBean.Type;
import com.ml.meliproxy.service.util.KeyUtil;

@Component
public class RulesBean {
	private static final Logger LOGGER = LoggerFactory.getLogger(RulesBean.class);

	@Autowired
	private CounterRepository counterRepository;

	@Autowired
	private AccessControlBean accessControlBean;

	@Autowired
	private ConfigRepository configRepository;

	private Set<Pattern> byPathPatterns;
	private Map<String, Set<Pattern>> byIpAndPathPatterns;
	private ConfigDoc currentConfig;
	private Long currentDefaultLimit = Long.MAX_VALUE;

	// Load en init del proxy y Reload cada 5 minutos, en caso de nueva version
	@Scheduled(fixedDelay = 360000, initialDelay = 0)
	public void reloadConfig() {
		try {
			ConfigDoc newConfig = configRepository.get();
			if (this.currentConfig == null || currentConfig.getVersion() == null
					|| !currentConfig.getVersion().equals(newConfig.getVersion())) {

				// Primero se calculan los patrones y luego se asignan. En caso de error, no
				// tendriamos una version de la config a medias.
				Set<Pattern> byPathPatternsTemp = toPatterns(newConfig.getByPath());
				Map<String, Set<Pattern>> byIpAndPathPatternsTemp = newConfig.getByIpAndPath().entrySet().stream()
						.collect(Collectors.toMap(Entry::getKey, e -> toPatterns(e.getValue())));

				this.byPathPatterns = byPathPatternsTemp;
				this.byIpAndPathPatterns = byIpAndPathPatternsTemp;
				this.currentConfig = newConfig;
				this.currentDefaultLimit = newConfig.getDefaultLimit() == null ? Long.MAX_VALUE
						: newConfig.getDefaultLimit();
				LOGGER.info("Config loaded. Version: {}", currentConfig.getVersion());
			}
		} catch (Exception e) {
			LOGGER.error("Error trying to reload configuration from database", e);
			if (this.currentDefaultLimit == null) {
				this.currentDefaultLimit = Long.MAX_VALUE;
			}
		}
	}

	private Set<Pattern> toPatterns(Map<String, Long> map) {
		return map.keySet().stream().map(Pattern::compile).collect(Collectors.toSet());
	}

	@Async
	public void incrementAndCheckLimits(String ip, String path) {
		incrementAndCheckLimits(ip, () -> limitByIp(ip), Type.IP);
		incrementAndCheckLimits(path, () -> limitByPath(path), Type.PATH);
		incrementAndCheckLimits(KeyUtil.combine(ip, path), () -> limitByIpAndPath(ip, path), Type.IP_PATH);
	}

	private void incrementAndCheckLimits(String key, Supplier<Long> limitSupplier, Type type) {
		Long current = this.counterRepository.increment(key);
		if (current >= limitSupplier.get()) {
			this.accessControlBean.block(key, type);
		}
	}

	private Long limitByIp(String ip) {
		if (this.currentConfig == null) {
			return this.currentDefaultLimit;
		}
		return this.currentConfig.getByIp().getOrDefault(ip, this.currentDefaultLimit);
	}

	private Long limitByPath(String path) {
		if (this.currentConfig == null) {
			return this.currentDefaultLimit;
		}

		return limitByPath(this.byPathPatterns, this.currentConfig.getByPath(), path);
	}

	private Long limitByIpAndPath(String ip, String path) {
		if (this.currentConfig == null) {
			return this.currentDefaultLimit;
		}

		Set<Pattern> patterns = byIpAndPathPatterns.getOrDefault(ip, new HashSet<>());
		Map<String, Long> byPathMap = this.currentConfig.getByIpAndPath().getOrDefault(ip, new HashMap<>());
		return limitByPath(patterns, byPathMap, path);
	}

	private Long limitByPath(Set<Pattern> patterns, Map<String, Long> byPathMap, String path) {
		return patterns.stream().filter(pattern -> pattern.matcher(path).matches()).findAny().map(Pattern::pattern)
				.map(byPathMap::get).orElse(this.currentDefaultLimit);
	}
}
