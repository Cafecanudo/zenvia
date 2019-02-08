package br.com.ilink.zenviaapi.models;

import br.com.ilink.zenviaapi.models.converters.LocalDateTimeDeserializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceivedMessage {

  @JsonProperty("id")
  private Long codigoInterno;
  @JsonProperty("dateReceived")
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime agendado;
  @JsonProperty("mobile")
  private String para;
  @JsonProperty("body")
  private String mensagem;
  @JsonProperty("shortcode")
  private String numeroEnvio;
  @JsonProperty("mobileOperatorName")
  private String operadora;
  private String mtId;

}
