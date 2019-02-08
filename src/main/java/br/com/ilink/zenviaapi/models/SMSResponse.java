package br.com.ilink.zenviaapi.models;

import br.com.ilink.zenviaapi.ZenviaAPI;
import com.fasterxml.jackson.annotation.JsonRootName;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonRootName("sendSmsResponse")
public class SMSResponse {

  private String statusCode;
  private String statusDescription;
  private String detailCode;
  private String detailDescription;
  private List<ZenviaAPI.ReceivedMessage> receivedMessages;
}
