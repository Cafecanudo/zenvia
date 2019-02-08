package br.com.ilink.zenviaapisoap.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SendSmsRequest {

  @JsonProperty("sendSmsRequests")
  public SMSRequestModel smsRequestModel;

}
