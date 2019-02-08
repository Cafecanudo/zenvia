package br.com.ilink.zenviaapisoap.models;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonRootName("sendSmsRequest")
public class SMSResponseModel {

  private String statusCode;
  private String statusDescription;
  private String detailCode;
  private String detailDescription;

}
