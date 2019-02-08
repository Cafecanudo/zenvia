package br.com.ilink.zenviaapisoap;

import br.com.ilink.zenviaapisoap.exceptions.ProcessorException;
import br.com.ilink.zenviaapisoap.exceptions.ValidationException;
import br.com.ilink.zenviaapisoap.models.SMSRequestModel;
import br.com.ilink.zenviaapisoap.models.SMSResponseModel;
import br.com.ilink.zenviaapisoap.models.SendSmsMultiRequest;
import br.com.ilink.zenviaapisoap.models.SendSmsMultiRequestParam;
import br.com.ilink.zenviaapisoap.models.SendSmsRequest;
import br.com.ilink.zenviaapisoap.utils.ConverterUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ZenviaAPI {

  private SMSRequestModel[] model;

  private ZenviaAPI(SMSRequestModel[] model) {
    this.model = model;
  }

  public static ZenviaAPI SMS(SMSRequestModel... model) {
    return new ZenviaAPI(model);
  }

  public SMSResponseModel sendUnique() {
    try {
      if (model.length > 1) {
        throw new ValidationException(
            "sendUnique enviar somente uma mensagem e você adicionou [" + model.length + "].");
      }
      JsonNode json = getJsonNode(RestService.SendUniqueSMS);
      if (json.has("sendSmsResponse") && json.hasNonNull("sendSmsResponse")) {
        return ConverterUtil
            .toObject(json.get("sendSmsResponse").toString(), SMSResponseModel.class);
      }
      return null;
    } catch (IOException e) {
      throw new ProcessorException(e);
    }
  }

  public List<SMSResponseModel> sendMultiple() {
    return sendMultiple(null);
  }

  public List<SMSResponseModel> sendMultiple(Integer aggregateId) {
    try {
      JsonNode json = getJsonNode(RestService.SendMultipleSMS, aggregateId);
      if (json.has("sendSmsMultiResponse") && json.hasNonNull("sendSmsMultiResponse")
          && json.get("sendSmsMultiResponse").has("sendSmsResponseList") && json
          .get("sendSmsMultiResponse").hasNonNull("sendSmsResponseList")) {
        TypeReference listType = new TypeReference<List<SMSResponseModel>>() {
        };
        List<SMSResponseModel> list = ConverterUtil
            .toObject(json.get("sendSmsMultiResponse").get("sendSmsResponseList").toString(),
                listType);
        return list;
      }
      return null;
    } catch (IOException e) {
      throw new ProcessorException(e);
    }
  }

  private JsonNode getJsonNode(RestService restService) {
    return getJsonNode(restService, null);
  }

  private JsonNode getJsonNode(RestService restService, Integer aggregateId) {
    if (Arrays.stream(model).collect(Collectors.toList()).isEmpty()) {
      throw new ValidationException("Requisição esta vazia!");
    }
    return ConverterUtil.toJson(
        HttpClient.POST(restService, converterObjeto(aggregateId).toString())
    );
  }

  private JsonNode converterObjeto(Integer aggregateId) {
    String json = ConverterUtil
        .toJsonString(SendSmsRequest.builder().smsRequestModel(model[0]).build());
    if (model.length > 1) {
      SendSmsMultiRequestParam build = SendSmsMultiRequestParam.builder()
          .aggregateId(aggregateId)
          .smsRequestList(Arrays.stream(model).collect(Collectors.toList()))
          .build();
      json = ConverterUtil
          .toJsonString(SendSmsMultiRequest.builder().sendSmsMultiRequestParam(build).build());
    }
    return ConverterUtil.toJson(json);
  }
}
