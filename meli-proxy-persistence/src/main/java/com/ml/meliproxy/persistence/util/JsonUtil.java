package com.ml.meliproxy.persistence.util;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;

public abstract class JsonUtil {
	public static final String DEFAULT_CHARSET = "UTF-8";

	private static final ObjectMapper objectMapper;

	static {
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new AfterburnerModule());
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		objectMapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
		objectMapper.enable(Feature.WRITE_BIGDECIMAL_AS_PLAIN);
		objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
		objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
	}

	public static <T> String serialize(T source) {
		if (source == null) {
			return null;
		}
		try {
			return objectMapper.writeValueAsString(source);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> T deserialize(String json, Class<T> clazz) {
		if (StringUtils.isBlank(json)) {
			return null;
		}
		try {
			return objectMapper.readValue(json, clazz);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
