package com.ml.meliproxy.persistence.repository;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.RawJsonDocument;
import com.ml.meliproxy.persistence.context.PersistenceConstants;
import com.ml.meliproxy.persistence.model.ConfigDoc;
import com.ml.meliproxy.persistence.util.JsonUtil;

@Repository
public class ConfigRepository {
	private final static String CONFIG_DOCUMEN_ID = "meli-proxy-config";

	@Resource(name = PersistenceConstants.COUCHBASE_COUNTERS_BUCKET)
	private Bucket bucket;

	public ConfigDoc get() {
		JsonDocument jsonDocument = bucket.get(CONFIG_DOCUMEN_ID);
		String json = jsonDocument.content().toString();
		return JsonUtil.deserialize(json, ConfigDoc.class);
	}

	public void save(ConfigDoc entity) {
		RawJsonDocument document = RawJsonDocument.create(CONFIG_DOCUMEN_ID, JsonUtil.serialize(entity));
		bucket.upsert(document);
	}
}