package br.com.ilink.zenviaapi.models;

import br.com.ilink.zenviaapi.ZenviaAPI;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendSmsResponse {

  @JsonProperty("sendSmsResponse")
  public ZenviaAPI.SMSResponse smsResponse;

}
