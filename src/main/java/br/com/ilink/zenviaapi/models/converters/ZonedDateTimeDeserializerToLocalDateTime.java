package br.com.ilink.zenviaapi.models.converters;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public class ZonedDateTimeDeserializerToLocalDateTime extends JsonDeserializer<LocalDateTime> {

  @Override
  public LocalDateTime deserialize(JsonParser jsonParser,
      DeserializationContext deserializationContext) throws IOException {
    if (jsonParser.getText().isEmpty()){
      return null;
    }
    return ZonedDateTime.parse(jsonParser.getText()).toLocalDateTime();
  }
}
