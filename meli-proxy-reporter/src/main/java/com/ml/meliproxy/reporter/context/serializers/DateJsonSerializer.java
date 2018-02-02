package com.ml.meliproxy.reporter.context.serializers;

import java.io.IOException;
import java.util.Date;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.util.StdDateFormat;

public class DateJsonSerializer
    extends JsonSerializer<Date> {
    private StdDateFormat stdDateFormat = new StdDateFormat();

    @Override
    public void serialize(Date value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeString(this.stdDateFormat.format(value));
    }
}
