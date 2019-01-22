package br.com.ilink.zenviaapi.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
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
@JsonRootName("sendSmsMultiRequest")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SendSmsMultiRequestParam {

  private Integer aggregateId;

  @JsonProperty("sendSmsRequestList")
  private List<SMSRequestModel> smsRequestList;
}
