package br.com.ilink.zenviaapi.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SMSReceivedResponse {

  @JsonProperty("receivedResponse")
  private SMSResponse smsResponse;

}
