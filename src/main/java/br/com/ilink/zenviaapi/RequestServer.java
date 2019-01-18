package br.com.ilink.zenviaapi;

import br.com.ilink.zenviaapi.exceptions.ProcessorException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Properties;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.codec.binary.Base64;

public class RequestServer {

  protected static Properties prop;
  public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

  static {
    try {
      prop = new Properties();
      prop.load(RequestServer.class.getResourceAsStream("/config.properties"));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  protected String POST(Object value) {
    OkHttpClient client = new OkHttpClient();
    RequestBody body = RequestBody.create(JSON, convertToJson(value));

    Request request = new Request.Builder()
        .url("https://httpbin.org/post")
//        .url(prop.getProperty("config.url"))
        .post(body)
        .build();
    try (Response response = client.newCall(request).execute()) {
      return response.body().string();
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  private String convertToJson(Object value) {
    try {
      return new ObjectMapper().writeValueAsString(value);
    } catch (JsonProcessingException e) {
      throw new ProcessorException(e);
    }
  }

  private String authorization() {
    return String.format("Basic %s",
        Base64.encodeBase64String(
            String.format("%s:%s", prop.getProperty("config.user"),
                prop.getProperty("config.password"))
                .getBytes()));
  }

}
