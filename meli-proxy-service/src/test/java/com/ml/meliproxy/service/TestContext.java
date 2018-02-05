package com.ml.meliproxy.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.mockito.Mockito;
import org.mockito.internal.util.collections.Sets;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.couchbase.client.java.Bucket;
import com.ml.meliproxy.persistence.context.PersistenceConstants;
import com.ml.meliproxy.persistence.model.BlockedDoc;
import com.ml.meliproxy.persistence.model.ConfigDoc;
import com.ml.meliproxy.persistence.repository.BlockedRepository;
import com.ml.meliproxy.persistence.repository.ConfigRepository;
import com.ml.meliproxy.persistence.repository.CounterRepository;
import com.ml.meliproxy.persistence.repository.RequestRepository;

@Configuration
@ComponentScan(basePackages = { "com.ml.meliproxy.service.component", "com.ml.meliproxy.service.proxy",
		"com.ml.meliproxy.service.context.config" })
public class TestContext {

	@Bean
	public BlockedRepository blockedRepository() {
		BlockedDoc blockedDoc = new BlockedDoc();
		blockedDoc.setByIp(Sets.newSet("192.168.1.10"));
		blockedDoc.setByPath(new HashSet<>());
		blockedDoc.setByIpAndPath(new HashSet<>());
		BlockedRepository mock = Mockito.mock(BlockedRepository.class);
		Mockito.when(mock.get()).thenReturn(blockedDoc);
		return mock;
	}

	@Bean
	public ConfigRepository configRepository() {
		ConfigRepository mock = Mockito.mock(ConfigRepository.class);
		ConfigDoc config = new ConfigDoc();
		config.setByIp(new HashMap<>());
		config.setByPath(byPath());
		config.setByIpAndPath( byIpAndPath());
		config.setVersion(1L);
		Mockito.when(mock.get()).thenReturn(config);
		return mock;
	}

	private HashMap<String, Long> byPath() {
		HashMap<String, Long> byPath = new HashMap<>();
		byPath.put("/byPath", 2L);
		return byPath;
	}

	private HashMap<String, Map<String, Long>> byIpAndPath() {
		HashMap<String, Map<String, Long>> byIpAndPath = new HashMap<>();
		HashMap<String, Long> byPath = new HashMap<>();
		byPath.put("/byIpAndPath", 50L);
		byIpAndPath.put("192.168.1.9", byPath);
		return byIpAndPath;
	}

	@Bean
	public CounterRepository counterRepository() {
		return new CounterRepository() {
			private Map<String, AtomicLong> storage = new HashMap<>();

			@Override
			public Long increment(String id, long delta) {
				AtomicLong counter = getCounter(id);
				return counter.addAndGet(delta);
			}

			@Override
			public Long get(String id) {
				AtomicLong counter = getCounter(id);
				return counter.get();
			}

			private AtomicLong getCounter(String id) {
				AtomicLong counter = storage.get(id);
				if (counter == null) {
					counter = new AtomicLong(0L);
					storage.put(id, counter);
				}
				return counter;
			}
		};
	}

	@Bean
	public RequestRepository requestRepository() {
		return Mockito.mock(RequestRepository.class);
	}

	@Bean(name = PersistenceConstants.COUCHBASE_COUNTERS_BUCKET)
	public Bucket couchbaseCountersBucket() throws Exception {
		return Mockito.mock(Bucket.class);
	}

	@Bean(name = PersistenceConstants.COUCHBASE_DATA_BUCKET)
	public Bucket couchbaseDataBucket() throws Exception {
		return Mockito.mock(Bucket.class);
	}
}
