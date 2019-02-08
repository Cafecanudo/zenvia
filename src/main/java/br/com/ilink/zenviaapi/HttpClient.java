package br.com.ilink.zenviaapi;

import br.com.ilink.zenviaapi.HttpClient.Param.Value;
import br.com.ilink.zenviaapi.exceptions.HttpClientException;
import br.com.ilink.zenviaapi.exceptions.UnauthorizedException;
import br.com.ilink.zenviaapi.models.SMSUserConfig;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.codec.binary.Base64;

public class HttpClient extends TrustConnection {

  private SMSUserConfig smsUserConfig;
  private static Properties prop;
  private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

  static {
    try {
      prop = new Properties();
      prop.load(HttpClient.class.getResourceAsStream("/zenvia-config.properties"));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static String POST(RestService restService, String value) {
    return new HttpClient()._POST(restService, value, null);
  }

  public static String POST(RestService restService, String value, SMSUserConfig smsUserConfig) {
    return new HttpClient()._POST(restService, value, smsUserConfig);
  }

  public static String GET(RestService restService, SMSUserConfig smsUserConfig, Value... parans) {
    return new HttpClient()._GET(restService, smsUserConfig, parans);
  }

  private String _GET(RestService restService, SMSUserConfig smsUserConfig, Value... parans) {
    this.smsUserConfig = smsUserConfig;

    List<Value> paths = Arrays.stream(parans)
        .filter(value -> value.path != null).collect(Collectors.toList());
    String pathsURL = paths.stream()
        .map(param -> param.path)
        .collect(Collectors.joining("/"));

    List<Value> querys = Arrays.stream(parans)
        .filter(value -> value.chave != null && value.value != null).collect(Collectors.toList());
    String query = querys.stream()
        .map(param -> String.format("%s=%s", param.chave, param.value))
        .collect(Collectors.joining("&"));

    Request request = createClient(restService.path + (!pathsURL.isEmpty() ? "/" + pathsURL : "") +
        (!query.isEmpty() ? "?" + query : ""))
        .addHeader("Accept", "application/json")
        .get()
        .build();
    return proccessResult(request);
  }

  private String _POST(RestService restService, String value, SMSUserConfig smsUserConfig) {
    this.smsUserConfig = smsUserConfig;

    RequestBody body = RequestBody.create(JSON, value);
    Request request = createClient(restService.path)
        .addHeader("Accept", "application/json")
        .post(body)
        .build();
    return proccessResult(request);
  }

  private String proccessResult(Request request) {
    OkHttpClient client = this.buildClient();
    try (Response response = client.newCall(request).execute()) {
      if (response.isSuccessful() || response.code() == 400) {
        return response.body().string();
      }
      if (response.code() == 401) {
        throw new UnauthorizedException(response.body().string());
      }
      throw new HttpClientException(response.body().string());
    } catch (IOException e) {
      throw new HttpClientException(e);
    }
  }

  private Builder createClient(String apped) {
    return new Builder()
        .url(prop.getProperty("config.zenvia.url") + apped);
  }

  @Override
  protected String authorization() {
    return String.format("Basic %s",
        Base64.encodeBase64String(
            String.format("%s:%s",
                ((smsUserConfig != null && smsUserConfig.getUsuario() != null) ? smsUserConfig
                    .getUsuario() : prop.getProperty("config.zenvia.user")),
                ((smsUserConfig != null && smsUserConfig.getUsuario() != null) ? smsUserConfig
                    .getUsuario() : prop.getProperty("config.zenvia.password"))).getBytes()));
  }

  public static class Param {

    private String chave;

    private Param(String chave) {
      this.chave = chave;
    }

    public static Param chave(String chave) {
      return new Param(chave);
    }

    public static Value path(String chave) {
      return new Param.Value(chave);
    }

    public Value value(String value) {
      return new Param.Value(this.chave, value);
    }

    public static class Value {

      private String path;
      private String chave;
      private String value;

      public Value(String path) {
        this.path = path;
      }

      public Value(String chave, String value) {
        this.chave = chave;
        this.value = value;
      }
    }
  }
}
