package br.com.ilink.zenviaapi.models;

import br.com.ilink.zenviaapi.BuilderValidation;
import br.com.ilink.zenviaapi.annotations.DefaultBooleanIfNULL;
import br.com.ilink.zenviaapi.annotations.DefaultDateIfNULL;
import br.com.ilink.zenviaapi.annotations.DefaultValueOptionIfNULL;
import br.com.ilink.zenviaapi.annotations.NotBlank;
import br.com.ilink.zenviaapi.annotations.OnlyNumber;
import br.com.ilink.zenviaapi.annotations.Size;
import br.com.ilink.zenviaapi.exceptions.ValidationException;
import br.com.ilink.zenviaapi.models.converters.LocalDateTimeSerializer;
import br.com.ilink.zenviaapi.models.enums.CallbackOptionEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SMSRequestModel {

  /**
   * Nome do remetente da mensagem
   */
  @Size(max = 50, message = "Quantidade máxima de {max} caracteres.")
  private String from;

  /**
   * Número do celular do destinatário incluíndo o DDI (55)
   */
  @NotBlank
  @OnlyNumber
  @Size(min = 12, max = 13)
  private String to;

  /**
   * Data e hora em que a mensagem deve ser enviada.
   */
  @DefaultDateIfNULL
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  private LocalDateTime schedule;

  /**
   * Texto da mensagem a ser enviada.
   */
  @NotBlank
  @Size(max = 152, message = "Quantidade máxima de 152 caracteres.")
  private String msg;

  /**
   * Tipo de status de entrega. ALL: Envia status intermediários e final da mensagem. FINAL: Envia o
   * status final de entrega da mensagem (recomendado). NONE: Não será feito callback do status de
   * entrega.
   */
  @DefaultValueOptionIfNULL
  private CallbackOptionEnum callbackOption;

  /**
   * Identificador da mensagem no sistema do cliente.
   */
  @Size(max = 15, message = "Quantidade máxima de {max} caracteres.")
  private String id;

  /**
   * Se sua conta utiliza o recurso de agregador de mensagens (centro de custo, campanha), este
   * parametro deve informar o código do agregador desejado.
   */
  @OnlyNumber
  @Size(max = 50)
  private String aggregateId;

  /**
   * Para o envio do Flash BuilderValidation a Zenvia API tem um parâmetro opcional para indicar que
   * a mensagem deve ser enviada como um Flash BuilderValidation. Este parâmetro chama-se flashSms e
   * aceita os valores true (como Flash BuilderValidation) ou false(como BuilderValidation normal) e
   * poderá ser informado tanto no envio único como no envio para vários destinatários
   */
  @DefaultBooleanIfNULL
  private Boolean flashSms;

  public static class SMSRequestModelBuilder extends BuilderValidation<SMSRequestModel> {

    @Override
    public SMSRequestModel build() throws ValidationException {
      return validarParametros(
          new SMSRequestModel(this.from, this.to, this.schedule, this.msg, this.callbackOption,
              this.id, this.aggregateId, this.flashSms)
      );
    }
  }
}
