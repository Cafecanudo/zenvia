package br.com.ilink.zenviaapi;

import br.com.ilink.zenviaapi.HttpClient.Param;
import br.com.ilink.zenviaapi.exceptions.ProcessorException;
import br.com.ilink.zenviaapi.exceptions.ValidationException;
import br.com.ilink.zenviaapi.models.SMSReceivedResponse;
import br.com.ilink.zenviaapi.models.SMSUserConfig;
import br.com.ilink.zenviaapi.models.SendSmsRequest;
import br.com.ilink.zenviaapi.models.SendSmsResponse;
import br.com.ilink.zenviaapi.models.SingleSMS;
import br.com.ilink.zenviaapi.models.enums.CallbackOptionEnum;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.NoArgsConstructor;

public class ZenviaAPI {

  @NoArgsConstructor
  public static class SMSRequest extends br.com.ilink.zenviaapi.models.SMSRequest {

    public SMSRequest(Set<String> para, String de, LocalDateTime agendado, String mensagem,
        CallbackOptionEnum callback, String codigoInterno, String codigoAgregado,
        Boolean smsUrgente) {
      super(para, de, agendado, mensagem, callback, codigoInterno, codigoAgregado, smsUrgente);
    }
  }

  public static class SMSResponse extends br.com.ilink.zenviaapi.models.SMSResponse {

  }

  public static class ReceivedMessage extends br.com.ilink.zenviaapi.models.ReceivedMessage {

  }

  public static EnviarSMS preparar(ZenviaAPI.SMSRequest req) {
    return new ZenviaAPI.EnviarSMS(req);
  }

  public static CheckSMSPeriodo preparar(LocalDateTime dataInicio, LocalDateTime dataFim) {
    return new ZenviaAPI.CheckSMSPeriodo(dataInicio, dataFim);
  }

  public static Config config(SMSUserConfig smsUserConfig) {
    return new ZenviaAPI.Config(smsUserConfig);
  }

  public static class Config {

    private SMSUserConfig smsUserConfig;

    public Config(SMSUserConfig smsUserConfig) {
      this.smsUserConfig = smsUserConfig;
    }

    public EnviarSMS preparar(ZenviaAPI.SMSRequest req) {
      return new ZenviaAPI.EnviarSMS(req, smsUserConfig);
    }

    public CheckSMSPeriodo preparar(LocalDateTime dataInicio, LocalDateTime dataFim) {
      return new ZenviaAPI.CheckSMSPeriodo(dataInicio, dataFim);
    }
  }

  /**
   * Classe para checar SMS
   */
  public static class CheckSMSPeriodo {

    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private SMSUserConfig smsUserConfig;

    public CheckSMSPeriodo(LocalDateTime dataInicio, LocalDateTime dataFim) {
      this.dataInicio = dataInicio;
      this.dataFim = dataFim;
    }

    public CheckSMSPeriodo(LocalDateTime dataInicio, LocalDateTime dataFim,
        SMSUserConfig smsUserConfig) {
      this.dataInicio = dataInicio;
      this.dataFim = dataFim;
      this.smsUserConfig = smsUserConfig;
    }

    public List<ZenviaAPI.ReceivedMessage> check() throws IOException {
      String json = HttpClient
          .GET(RestService.CheckSMS, smsUserConfig,
              Param.path(dataInicio.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))),
              Param.path(dataFim.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))
          );
      br.com.ilink.zenviaapi.models.SMSResponse listaReceive = ConverterUtil
          .toObject(json, SMSReceivedResponse.class).getSmsResponse();
      return listaReceive.getReceivedMessages() != null ? listaReceive.getReceivedMessages()
          : new ArrayList<>();
    }
  }

  /**
   * Classe de envio SMS
   */
  public static class EnviarSMS {

    private SMSRequest req;
    private SMSUserConfig smsUserConfig;

    public EnviarSMS(SMSRequest req) {
      this.req = req;
    }

    public EnviarSMS(SMSRequest req, SMSUserConfig smsUserConfig) {
      this.req = req;
      this.smsUserConfig = smsUserConfig;
    }

    public List<ZenviaAPI.SMSResponse> enviar() {
      if (req == null) {
        throw new ValidationException("Requisição está fazia");
      }
      return req.getPara().stream()
          .map(phone ->
              SendSmsRequest.builder()
                  .singleSMS(
                      SingleSMS.builder()
                          .to(phone)
                          .msg(req.getMensagem())
                          .schedule(req.getAgendado())
                          .id(req.getCodigoInterno())
                          .aggregateId(req.getCodigoAgregado())
                          .from(req.getDe())
                          .callbackOption(req.getCallback().name())
                          .flashSms(req.getSmsUrgente())
                          .build()
                  ).build()
          )
          .map(this::processar)
          .collect(Collectors.toList());
    }

    private ZenviaAPI.SMSResponse processar(SendSmsRequest sms) {
      try {
        String json = HttpClient
            .POST(RestService.SendUniqueSMS, ConverterUtil.toJsonString(sms), smsUserConfig);
        return ConverterUtil.toObject(json, SendSmsResponse.class).getSmsResponse();
      } catch (IOException e) {
        throw new ProcessorException(e);
      }
    }
  }

}
