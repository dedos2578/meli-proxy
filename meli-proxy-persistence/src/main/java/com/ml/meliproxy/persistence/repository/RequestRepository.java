package com.ml.meliproxy.persistence.repository;

import java.util.UUID;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.document.RawJsonDocument;
import com.ml.meliproxy.persistence.context.PersistenceConstants;
import com.ml.meliproxy.persistence.model.RequestDoc;
import com.ml.meliproxy.persistence.util.JsonUtil;

@Repository
public class RequestRepository {

	@Resource(name = PersistenceConstants.COUCHBASE_DATA_BUCKET)
	private Bucket bucket;

	public void save(RequestDoc entity) {
		String id = UUID.randomUUID().toString();
		RawJsonDocument document = RawJsonDocument.create(id, JsonUtil.serialize(entity));
		bucket.upsert(document);
	}
}