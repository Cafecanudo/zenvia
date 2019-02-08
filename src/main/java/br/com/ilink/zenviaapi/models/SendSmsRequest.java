package br.com.ilink.zenviaapi.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SendSmsRequest {

  @JsonProperty("sendSmsRequest")
  public SingleSMS singleSMS;

}
