package com.ml.meliproxy.service.context.config;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
@EnableScheduling
public class ExecutorConfig {

	@Value("${async.executor.pool.size:64}")
	private int poolSize;

	@Value("${async.executor.thread-name-prefix:check-async-}")
	private String threadNamePrefix;

	@Value("${async.executor.pool.size:32}")
	private int reporterPoolSize;

	@Value("${async.executor.thread-name-prefix:reporter-async-}")
	private String reporterThreadNamePrefix;
	
	@Value("${async.executor.await-termination:60}")
	private int awaitTermination;

	@Bean(name = "checkExecutor", destroyMethod = "shutdown")
	public Executor getAsyncExecutor() {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setThreadNamePrefix(threadNamePrefix);
		taskExecutor.setCorePoolSize(poolSize);
		taskExecutor.setMaxPoolSize(poolSize);
		taskExecutor.setAwaitTerminationSeconds(awaitTermination);
		taskExecutor.initialize();
		return Executors.newCachedThreadPool();
	}

	@Bean(name = "reporterExecutor", destroyMethod = "shutdown")
	public Executor getReporterExecutor() {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setThreadNamePrefix(reporterThreadNamePrefix);
		taskExecutor.setCorePoolSize(reporterPoolSize);
		taskExecutor.setMaxPoolSize(reporterPoolSize);
		taskExecutor.setAwaitTerminationSeconds(awaitTermination);
		taskExecutor.initialize();
		return taskExecutor.getThreadPoolExecutor();
	}
}
