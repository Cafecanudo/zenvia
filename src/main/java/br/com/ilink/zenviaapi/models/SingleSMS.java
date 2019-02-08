package br.com.ilink.zenviaapi.models;

import br.com.ilink.zenviaapi.models.converters.LocalDateTimeSerializer;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SingleSMS {

  private String from;
  private String to;
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  private LocalDateTime schedule;
  private String msg;
  private String callbackOption;
  private String id;
  private String aggregateId;
  private boolean flashSms;

}
