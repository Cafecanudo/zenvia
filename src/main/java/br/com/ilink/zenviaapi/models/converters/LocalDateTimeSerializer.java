package br.com.ilink.zenviaapi.models.converters;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {

  @Override
  public void serialize(LocalDateTime localDateTime, JsonGenerator jsonGenerator,
      SerializerProvider serializerProvider) throws IOException {
    jsonGenerator
        .writeString(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss").format(localDateTime));
  }
}
