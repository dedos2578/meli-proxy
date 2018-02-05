package com.ml.meliproxy.persistence.repository;

import static com.couchbase.client.java.query.Select.select;
import static com.couchbase.client.java.query.dsl.Expression.x;

import java.util.UUID;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.document.RawJsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryRow;
import com.couchbase.client.java.query.Statement;
import com.ml.meliproxy.persistence.context.PersistenceConstants;
import com.ml.meliproxy.persistence.model.RequestDoc;
import com.ml.meliproxy.persistence.util.JsonUtil;

@Repository
public class RequestRepository {

	@Resource(name = PersistenceConstants.COUCHBASE_DATA_BUCKET)
	private Bucket bucket;

	@Value("${couchbase.data-bucket:meli_proxy}")
	public String bucketName;

	public void save(RequestDoc entity) {
		String id = UUID.randomUUID().toString();
		RawJsonDocument document = RawJsonDocument.create(id, JsonUtil.serialize(entity));
		bucket.upsert(document);
	}

	public Stream<String> getByIp(String ip) {
		Statement statement = select("count(*) AS count", "`path`", "http_status").from(bucketName)
				.where(x("ip").eq(x("$ip"))).groupBy("`path`", "http_status");
		JsonObject placeholderValues = JsonObject.create().put("ip", ip);
		N1qlQuery query = N1qlQuery.parameterized(statement, placeholderValues);
		return executeQuery(query);
	}

	public Stream<String> allIps() {
		N1qlQuery query = N1qlQuery
				.simple("select count(*) as count, ip from " + bucketName + " where ip is not null GROUP BY ip");
		return executeQuery(query);
	}

	public Stream<String> getByPath(String path) {
		Statement statement = select("count(*) AS count", "ip", "http_status").from(bucketName)
				.where(x("`path`").eq(x("$path"))).groupBy("ip", "http_status");
		JsonObject placeholderValues = JsonObject.create().put("path", path);
		N1qlQuery query = N1qlQuery.parameterized(statement, placeholderValues);
		return executeQuery(query);
	}

	public Stream<String> allPaths() {
		N1qlQuery query = N1qlQuery.simple(
				"select count(*) as count, `path` from " + bucketName + " where `path` is not null GROUP BY `path`");
		return executeQuery(query);
	}

	public Stream<String> statistics() {
		N1qlQuery query = N1qlQuery.simple(
				"select count(*) as count, avg(proxy_time) AS proxy_time_avg, avg(service_time) AS service_time_avg, "
						+ "avg(proxy_time - service_time) AS diff_avg, `path`, http_status FROM " + bucketName
						+ " WHERE date is not null GROUP BY `path`, http_status");
		return executeQuery(query);
	}

	private Stream<String> executeQuery(N1qlQuery query) {
		return StreamSupport.stream(bucket.query(query).spliterator(), false).map(N1qlQueryRow::value)
				.map(JsonObject::toString);
	}
}