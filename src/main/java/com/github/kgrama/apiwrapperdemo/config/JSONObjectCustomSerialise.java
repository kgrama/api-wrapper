package com.github.kgrama.apiwrapperdemo.config;

import java.io.IOException;

import org.json.JSONObject;
import org.json.JSONWriter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class JSONObjectCustomSerialise extends JsonSerializer<JSONObject> {

	@Override
	public void serialize(JSONObject value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		try {
			gen.writeEmbeddedObject(JSONWriter.valueToString(value));
		} catch (Exception e) {
			log.error("{}", e);
			gen.writeStartObject();
			gen.writeEndObject();
		}
	}

}
