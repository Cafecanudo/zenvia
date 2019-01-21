package br.com.ilink.zenviaapi;

import br.com.ilink.zenviaapi.exceptions.ProcessorException;
import br.com.ilink.zenviaapi.models.SMSRequestModel;
import br.com.ilink.zenviaapi.models.SMSResponseModel;
import br.com.ilink.zenviaapi.utils.ConverterUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.util.List;

public class ZenviaAPI {

  private SMSRequestModel model;

  private ZenviaAPI(SMSRequestModel model) {
    this.model = model;
  }

  public static ZenviaAPI SMS(SMSRequestModel model) {
    return new ZenviaAPI(model);
  }

  public SMSResponseModel sendUnique() {
    try {
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
    try {
      JsonNode json = getJsonNode(RestService.SendMultipleSMS);
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
    String data = HttpClient.POST(restService, ConverterUtil.toJson(model));
    return ConverterUtil.toJson(data);
  }
}
