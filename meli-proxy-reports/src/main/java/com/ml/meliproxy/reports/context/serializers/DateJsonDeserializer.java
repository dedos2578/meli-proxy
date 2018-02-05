package com.ml.meliproxy.reports.context.serializers;

import java.io.IOException;
import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.util.StdDateFormat;

public class DateJsonDeserializer extends JsonDeserializer<Date> {
	private StdDateFormat stdDateFormat = new StdDateFormat();

	@Override
	public Date deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
		JsonToken t = jp.getCurrentToken();
		if (t == JsonToken.VALUE_STRING) {

			String str = jp.getText().trim();
			if (str.length() == 0) { // [JACKSON-360]
				return null;
			}

			try {
				return this.stdDateFormat.parse(str);
			} catch (Exception ignored) {
				throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Invalid date format \'" + str + "\'.");
			}

		} else {
			throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Invalid date format.");
		}
	}
}
