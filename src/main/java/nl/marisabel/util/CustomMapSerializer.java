package nl.marisabel.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.Map;

public class CustomMapSerializer extends JsonSerializer<Map<String, Double>> {
 @Override
 public void serialize(Map<String, Double> map, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
  jsonGenerator.writeStartObject();
  for (Map.Entry<String, Double> entry : map.entrySet()) {
   jsonGenerator.writeFieldName(entry.getKey());
   jsonGenerator.writeNumber(entry.getValue());
  }
  jsonGenerator.writeEndObject();
 }
}