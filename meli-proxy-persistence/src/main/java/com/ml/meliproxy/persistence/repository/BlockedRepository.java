package com.ml.meliproxy.persistence.repository;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.document.JsonDocument;
import com.ml.meliproxy.persistence.context.PersistenceConstants;
import com.ml.meliproxy.persistence.model.BlockedDoc;
import com.ml.meliproxy.persistence.util.JsonUtil;

@Repository
public class BlockedRepository {
	private final static String CONFIG_DOCUMEN_ID = "meli-proxy-blocked";

	@Resource(name = PersistenceConstants.COUCHBASE_COUNTERS_BUCKET)
	private Bucket bucket;

	public BlockedDoc get() {
		JsonDocument jsonDocument = bucket.get(CONFIG_DOCUMEN_ID);
		String json = jsonDocument.content().toString();
		return JsonUtil.deserialize(json, BlockedDoc.class);
	}

	public void addByIp(String ip) {
		bucket.mutateIn(CONFIG_DOCUMEN_ID).arrayAddUnique("by_ip", ip).execute();
	}

	public void addByPath(String path) {
		bucket.mutateIn(CONFIG_DOCUMEN_ID).arrayAddUnique("by_path", path).execute();
	}

	public void addByIpAndPath(String combinedKey) {
		bucket.mutateIn(CONFIG_DOCUMEN_ID).arrayAddUnique("by_ip_and_path", combinedKey).execute();
	}
}