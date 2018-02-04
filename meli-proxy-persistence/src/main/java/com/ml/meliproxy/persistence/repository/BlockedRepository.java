package com.ml.meliproxy.persistence.repository;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.error.subdoc.MultiMutationException;
import com.couchbase.client.java.error.subdoc.PathExistsException;
import com.ml.meliproxy.persistence.context.PersistenceConstants;
import com.ml.meliproxy.persistence.model.BlockedDoc;
import com.ml.meliproxy.persistence.util.JsonUtil;

@Repository
public class BlockedRepository {
	private static final Logger LOGGER = LoggerFactory.getLogger(BlockedRepository.class);

	private final static String CONFIG_DOCUMEN_ID = "meli-proxy-blocked";

	@Resource(name = PersistenceConstants.COUCHBASE_COUNTERS_BUCKET)
	private Bucket bucket;

	public BlockedDoc get() {
		JsonDocument jsonDocument = bucket.get(CONFIG_DOCUMEN_ID);
		String json = jsonDocument.content().toString();
		return JsonUtil.deserialize(json, BlockedDoc.class);
	}

	public void addByIp(String ip) {
		arrayAddUnique("by_ip", ip);
	}

	public void addByPath(String path) {
		arrayAddUnique("by_path", path);
	}

	public void addByIpAndPath(String combinedKey) {
		arrayAddUnique("by_ip_and_path", combinedKey);
	}

	private void arrayAddUnique(String field, String token) {
		try {
			bucket.mutateIn(CONFIG_DOCUMEN_ID).arrayAddUnique(field, token).execute();
		} catch (PathExistsException | MultiMutationException e) {
			LOGGER.info("Lock already informed. {}", token);
		}
	}
}