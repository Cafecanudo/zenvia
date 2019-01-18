package br.com.ilink.zenviaapi.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SMSResponse {

  private String statusCode;
  private String statusDescription;
  private String detailCode;
  private String detailDescription;

}
