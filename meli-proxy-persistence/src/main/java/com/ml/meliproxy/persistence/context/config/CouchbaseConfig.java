package com.ml.meliproxy.persistence.context.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.env.CouchbaseEnvironment;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;
import com.ml.meliproxy.persistence.context.PersistenceConstants;

@Configuration
public class CouchbaseConfig {

	@Value("#{'${couchbase.hosts:127.0.0.1}'.split(',')}")
	private List<String> hosts;

	@Value("${couchbase.data-bucket:meli_proxy}")
	public String bucketName;

	@Value("${couchbase.counters-bucket:meli_proxy_counters}")
	public String countersBucketName;

	@Value("${couchbase.username:admin}")
	public String username;

	@Value("${couchbase.password:password}")
	public String password;

	@Value("${couchbase.kv-timeout:5000}")
	public long kvTimeout;

	@Value("${couchbase.kv-endpoints:2}")
	public int kvEndpoints;

	@Bean(destroyMethod = "disconnect")
	public Cluster couchbaseCluster() throws Exception {
		CouchbaseEnvironment environment = DefaultCouchbaseEnvironment.builder().kvTimeout(this.kvTimeout)
				.kvEndpoints(this.kvEndpoints).build();

		Cluster cluster = CouchbaseCluster.create(environment, this.hosts);
		cluster.authenticate(this.username, this.password);
		return cluster;
	}

	@Bean(destroyMethod = "close", name = PersistenceConstants.COUCHBASE_COUNTERS_BUCKET)
	public Bucket couchbaseCountersBucket() throws Exception {
		return this.couchbaseCluster().openBucket(this.countersBucketName);
	}

	@Bean(destroyMethod = "close", name = PersistenceConstants.COUCHBASE_DATA_BUCKET)
	public Bucket couchbaseDataBucket() throws Exception {
		return this.couchbaseCluster().openBucket(this.bucketName);
	}
}
