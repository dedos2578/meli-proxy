package com.ml.meliproxy.service.component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
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
import com.ml.meliproxy.service.util.KeyUtil;

@Component
public class RulesBean {
	private static final long MAX_BUFFER_SIZE = 100L;

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

	private Map<String, AtomicLong> limitBufferMap = new HashMap<>();
	private Map<String, Long> lastCountMap = new HashMap<>();

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

	public void increment(String ip, String path) {
		getLimitBuffer(ip).incrementAndGet();
		getLimitBuffer(path).incrementAndGet();
		getLimitBuffer(KeyUtil.combine(ip, path)).incrementAndGet();
	}

	@Async("checkExecutor")
	public void checkLimits(String ip, String path) {
		incrementAndCheckLimits(ip, limitByIp(ip), accessControlBean::blockByIp);
		incrementAndCheckLimits(path, limitByPath(path), accessControlBean::blockByPath);
		incrementAndCheckLimits(KeyUtil.combine(ip, path), limitByIpAndPath(ip, path),
				accessControlBean::blockByIpAndPath);
	}

	// El buffer hace que el contador sume de a 100 y acelera en caso de estar cerca
	// del limite. Si bien con mayor hardware es probable que el problema que estoy
	// solucionando aqui no ocurra, es una forma controlar los counts contra la
	// base.
	private void incrementAndCheckLimits(String key, long limit, Consumer<String> blocker) {
		long currentBuffer = getLimitBuffer(key).get();
		long pessimisticCounter = getLastCount(key) + (currentBuffer * 3);

		if ((currentBuffer > 0 && pessimisticCounter > limit) || currentBuffer > MAX_BUFFER_SIZE) {
			checkAndBlockIfNecessary(key, blocker, currentBuffer, limit);
		}
	}

	private synchronized void checkAndBlockIfNecessary(String key, Consumer<String> blocker, long currentBuffer,
			long limit) {
		long newCurrentBuffer = getLimitBuffer(key).get();
		if (newCurrentBuffer >= currentBuffer) {
			getLimitBuffer(key).addAndGet(-newCurrentBuffer);
			long current = this.counterRepository.increment(key, newCurrentBuffer);
			lastCountMap.put(key, current);
			LOGGER.info("Checking... {} buffer: {} current: {}", key, newCurrentBuffer, current);

			if (current >= limit) {
				blocker.accept(key);
			}
		}
	}

	private Long getLastCount(String key) {
		Long value = lastCountMap.get(key);
		if (value != null) {
			return value;
		}

		synchronized (this) {
			value = lastCountMap.get(key);
			if (value == null) {
				value = counterRepository.get(key);
				lastCountMap.put(key, value);
			}
			return value;
		}
	}

	private AtomicLong getLimitBuffer(String key) {
		AtomicLong value = limitBufferMap.get(key);
		if (value != null) {
			return value;
		}

		synchronized (this) {
			value = limitBufferMap.get(key);
			if (value == null) {
				value = new AtomicLong(0L);
				limitBufferMap.put(key, value);
			}
			return value;
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

	private Set<Pattern> toPatterns(Map<String, Long> map) {
		return map.keySet().stream().map(Pattern::compile).collect(Collectors.toSet());
	}
}
