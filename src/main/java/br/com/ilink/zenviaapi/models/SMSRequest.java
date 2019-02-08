package br.com.ilink.zenviaapi.models;

import br.com.ilink.zenviaapi.ZenviaAPI;
import br.com.ilink.zenviaapi.annotations.DefaultBooleanIfNULL;
import br.com.ilink.zenviaapi.annotations.DefaultDateIfNULL;
import br.com.ilink.zenviaapi.annotations.DefaultValueOptionIfNULL;
import br.com.ilink.zenviaapi.annotations.ListNotBlank;
import br.com.ilink.zenviaapi.annotations.ListOnlyNumber;
import br.com.ilink.zenviaapi.annotations.NotBlank;
import br.com.ilink.zenviaapi.annotations.OnlyNumber;
import br.com.ilink.zenviaapi.annotations.Size;
import br.com.ilink.zenviaapi.annotations.SizeEachList;
import br.com.ilink.zenviaapi.annotations.SizeList;
import br.com.ilink.zenviaapi.exceptions.ValidationException;
import br.com.ilink.zenviaapi.models.converters.LocalDateTimeSerializer;
import br.com.ilink.zenviaapi.models.enums.CallbackOptionEnum;
import br.com.ilink.zenviaapi.BuilderValidation;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SMSRequest {

  /**
   * Número do celular do destinatário incluíndo o DDI (55)
   */
  @SizeList
  @ListNotBlank
  @ListOnlyNumber
  @SizeEachList(min = 12, max = 13)
  @Default
  private Set<String> para = new HashSet<>();

  /**
   * Nome do remetente da mensagem
   */
  @Size(max = 50, message = "Quantidade máxima de {max} caracteres.")
  private String de;

  /**
   * Data e hora em que a mensagem deve ser enviada.
   */
  @DefaultDateIfNULL
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  private LocalDateTime agendado;

  /**
   * Texto da mensagem a ser enviada.
   */
  @NotBlank
  @Size(max = 152, message = "Quantidade máxima de 152 caracteres.")
  private String mensagem;

  /**
   * Tipo de status de entrega. ALL: Envia status intermediários e final da mensagem. FINAL: Envia o
   * status final de entrega da mensagem (recomendado). NONE: Não será feito callback do status de
   * entrega.
   */
  @DefaultValueOptionIfNULL
  private CallbackOptionEnum callback;

  /**
   * Identificador da mensagem no sistema do cliente.
   */
  @NotBlank
  @Size(max = 15, message = "Quantidade máxima de {max} caracteres.")
  private String codigoInterno;

  /**
   * Se sua conta utiliza o recurso de agregador de mensagens (centro de custo, campanha), este
   * parametro deve informar o código do agregador desejado.
   */
  @OnlyNumber
  @Size(max = 50)
  private String codigoAgregado;

  /**
   * Para o envio do Flash BuilderValidation a Zenvia API tem um parâmetro opcional para indicar que
   * a mensagem deve ser enviada como um Flash BuilderValidation. Este parâmetro chama-se flashSms e
   * aceita os valores true (como Flash BuilderValidation) ou false(como BuilderValidation normal) e
   * poderá ser informado tanto no envio único como no envio para vários destinatários
   */
  @DefaultBooleanIfNULL
  private Boolean smsUrgente;

  public SMSRequest addPara(String phone) {
    if (phone != null) {
      this.para.add(phone);
    }
    return this;
  }

  public void agendado(LocalDateTime agendado) {
    this.agendado = agendado;
  }

  public static class SMSRequestBuilder extends BuilderValidation<ZenviaAPI.SMSRequest> {

    private Set<String> para = new HashSet<>();

    public ZenviaAPI.SMSRequest.SMSRequestBuilder para(Set<String> phone) {
      if (phone != null && !phone.isEmpty()) {
        this.para.addAll(phone);
      }
      return this;
    }

    public ZenviaAPI.SMSRequest.SMSRequestBuilder para(String phone) {
      if (phone != null) {
        this.para.add(phone);
      }
      return this;
    }

    public ZenviaAPI.SMSRequest.SMSRequestBuilder agendado(LocalDateTime agendado) {
      this.agendado = agendado;
      return this;
    }

    public ZenviaAPI.SMSRequest.SMSRequestBuilder agendado(Date agendado) {
      this.agendado = agendado.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
      return this;
    }

    @Override
    public ZenviaAPI.SMSRequest build() throws ValidationException {
      return validarParametros(
          new ZenviaAPI.SMSRequest(this.para, this.de, this.agendado, this.mensagem,
              this.callback, this.codigoInterno, this.codigoAgregado, this.smsUrgente)
      );
    }
  }
}
