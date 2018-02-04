package com.ml.meliproxy.persistence.repository;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.document.JsonLongDocument;
import com.ml.meliproxy.persistence.context.PersistenceConstants;

@Repository
public class CounterRepository {

	@Resource(name = PersistenceConstants.COUCHBASE_COUNTERS_BUCKET)
	private Bucket bucket;

	public Long increment(String id) {
		JsonLongDocument counter = bucket.counter(id, 1, 1);
		return counter.content();
	}
}