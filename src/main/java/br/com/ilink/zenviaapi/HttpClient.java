package br.com.ilink.zenviaapi;

import br.com.ilink.zenviaapi.exceptions.HttpClientException;
import java.io.IOException;
import java.util.Properties;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.codec.binary.Base64;

public class HttpClient extends TrustConnection {

  private static Properties prop;
  private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

  static {
    try {
      prop = new Properties();
      prop.load(HttpClient.class.getResourceAsStream("/config.properties"));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static String POST(RestService restService, String value) {
    return new HttpClient()._POST(restService, value);
  }

  private String _POST(RestService restService, String value) {
    OkHttpClient client = this.buildClient();
    RequestBody body = RequestBody.create(JSON, value);

    Request request = new Request.Builder()
        .url(prop.getProperty("config.url") + restService.path)
        .post(body)
        .build();
    try (Response response = client.newCall(request).execute()) {
      return response.body().string();
    } catch (IOException e) {
      throw new HttpClientException(e);
    }
  }

  @Override
  protected String authorization() {
    return String.format("Basic %s",
        Base64.encodeBase64String(
            String.format("%s:%s", prop.getProperty("config.user"),
                prop.getProperty("config.password"))
                .getBytes()));
  }

}
