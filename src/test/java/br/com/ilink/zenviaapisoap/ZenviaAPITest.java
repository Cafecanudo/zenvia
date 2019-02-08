package br.com.ilink.zenviaapisoap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.any;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import br.com.ilink.zenviaapisoap.exceptions.HttpClientException;
import br.com.ilink.zenviaapisoap.exceptions.ValidationException;
import br.com.ilink.zenviaapisoap.models.SMSRequestModel;
import br.com.ilink.zenviaapisoap.models.SMSResponseModel;
import br.com.ilink.zenviaapisoap.utils.ConverterUtil;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okhttp3.Response;
import okio.Timeout;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"javax.net.ssl.*", "javax.security.*"})
@PrepareForTest({HttpClient.class, TrustConnection.class})
public class ZenviaAPITest {

  @Mock
  Properties prop;
  SMSRequestModel req;

  @Rule
  ExpectedException expectedException = ExpectedException.none();

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    req = SMSRequestModel.builder()
        .to("5562994427822")
        .msg("Mensagem de teste")
        .schedule(LocalDateTime
            .parse("15/04/2019 15:45:22", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")))
        .build();
  }

  @Test
  public void testarEnvioDeUnicoSMS() {
    mockStatic(HttpClient.class);
    when(HttpClient.POST(any(), any()))
        .thenReturn("{"
            + "  \"sendSmsResponse\": {"
            + "    \"statusCode\": \"00\","
            + "    \"statusDescription\": \"Ok\","
            + "    \"detailCode\": \"000\","
            + "    \"detailDescription\": \"Message Sent\""
            + "  }"
            + "}");

    SMSResponseModel response = ZenviaAPI.SMS(req).sendUnique();
    assertThat(response, is(notNullValue()));
  }

  @Test
  public void testarEnvioDeMultiplosSMS() {
    mockStatic(HttpClient.class);
    when(HttpClient.POST(any(), any()))
        .thenReturn("{\n"
            + "  \"sendSmsMultiResponse\": {\n"
            + "    \"sendSmsResponseList\": [\n"
            + "      {\n"
            + "        \"statusCode\": \"00\",\n"
            + "        \"statusDescription\": \"Ok\",\n"
            + "        \"detailCode\": \"000\",\n"
            + "        \"detailDescription\": \"Message Sent\"\n"
            + "      },\n"
            + "      {\n"
            + "        \"statusCode\": \"00\",\n"
            + "        \"statusDescription\": \"Ok\",\n"
            + "        \"detailCode\": \"000\",\n"
            + "        \"detailDescription\": \"Message Sent\"\n"
            + "      }\n"
            + "    ]\n"
            + "  }\n"
            + "}");

    List<SMSResponseModel> list = ZenviaAPI.SMS(req).sendMultiple();
    assertThat(list, is(notNullValue()));
    assertThat(list, not(empty()));
    assertThat(list, hasSize(2));
  }

  @Test
  public void testarSaidaDeConvertaoUnicoItem() throws Exception {
    SMSRequestModel[] model = {req, req};
    ZenviaAPI zenviaAPI = spy(ZenviaAPI.SMS(model));

    JsonNode json = Whitebox.invokeMethod(zenviaAPI,
        "converterObjeto", 1234);

    assertThat(json, is(notNullValue()));
    assertThat(json, is(ConverterUtil.toJson("{\n"
        + "  \"sendSmsMultiRequest\": {\n"
        + "    \"aggregateId\": 1234,\n"
        + "    \"sendSmsRequestList\": [\n"
        + "      {\n"
        + "        \"to\": \"5562994427822\",\n"
        + "        \"schedule\": \"2019-04-15T15:45:22\",\n"
        + "        \"msg\": \"Mensagem de teste\",\n"
        + "        \"callbackOption\": \"NONE\",\n"
        + "        \"flashSms\": false\n"
        + "      },\n"
        + "      {\n"
        + "        \"to\": \"5562994427822\",\n"
        + "        \"schedule\": \"2019-04-15T15:45:22\",\n"
        + "        \"msg\": \"Mensagem de teste\",\n"
        + "        \"callbackOption\": \"NONE\",\n"
        + "        \"flashSms\": false\n"
        + "      }\n"
        + "    ]\n"
        + "  }\n"
        + "}")));
  }

  @Test
  public void testarEnvioComListaVazia() {
    expectedException.expect(ValidationException.class);
    expectedException.expectMessage("Requisição esta vazia!");

    ZenviaAPI.SMS().sendUnique();
  }

  @Test
  public void testarEnvioUnicoComListaMaiorQue2() {
    expectedException.expect(ValidationException.class);
    expectedException.expectMessage("sendUnique enviar somente uma mensagem e você adicionou [3].");

    ZenviaAPI.SMS(req, req, req).sendUnique();
  }

  @Test
  public void testarExcecoesServicosUnknownHostException() throws Exception {
    mockStatic(HttpClient.class);
    doThrow(new HttpClientException(new UnknownHostException("https://nao-existe-isso")))
        .when(HttpClient.class, "POST", any(), any());

    when(prop, "getProperty", "config.url")
        .thenReturn("https://nao-existe-isso");
    Whitebox.setInternalState(HttpClient.class, "prop", prop);

    expectedException.expect(HttpClientException.class);
    expectedException.expectMessage("java.net.UnknownHostException: https://nao-existe-isso");

    ZenviaAPI.SMS(req).sendUnique();
  }

  @Test
  public void testarExcecoesServicosSocketTimeoutException() throws Exception {
    expectedException.expect(HttpClientException.class);
    expectedException.expectMessage("java.io.InterruptedIOException: timeout");

    when(prop, "getProperty", "config.url")
        .thenReturn("http://www.nao-existe-isso.com.br");
    Whitebox.setInternalState(HttpClient.class, "prop", prop);

    HttpClient httpClient = spy(new HttpClient());
    when(httpClient, "buildClient").thenReturn(new OkHttpClient.Builder()
        .callTimeout(1L, TimeUnit.MILLISECONDS)
        .hostnameVerifier((host, session) -> true)
        .build());

    whenNew(HttpClient.class).withNoArguments().thenReturn(httpClient);
    ZenviaAPI.SMS(req).sendUnique();
  }

  @Test
  public void testarExcecoesServicosConnectionTimeoutException() throws Exception {
    expectedException.expect(HttpClientException.class);
    expectedException.expectMessage("java.net.SocketTimeoutException");

    when(prop, "getProperty", "config.url")
        .thenReturn("https://private-anon-7f7805d844-zenviasms.apiary-mock.com/services");
    Whitebox.setInternalState(HttpClient.class, "prop", prop);

    HttpClient httpClient = spy(new HttpClient());
    when(httpClient, "buildClient").thenReturn(new OkHttpClient.Builder()
        .connectTimeout(1L, TimeUnit.MILLISECONDS)
        .hostnameVerifier((host, session) -> true)
        .build());

    whenNew(HttpClient.class).withNoArguments().thenReturn(httpClient);
    ZenviaAPI.SMS(req, req).sendMultiple();
  }

  @Test
  public void forceIOExceptionEmChamadaServico() throws Exception {
    OkHttpClient okClient = spy(new Builder().build());

    Call resp = spy(new Call() {
      @Override
      public Request request() {
        return null;
      }

      @Override
      public Response execute() throws IOException {
        throw new IOException("Deu erro no IO");
      }

      @Override
      public void enqueue(Callback callback) {

      }

      @Override
      public void cancel() {

      }

      @Override
      public boolean isExecuted() {
        return false;
      }

      @Override
      public boolean isCanceled() {
        return false;
      }

      @Override
      public Timeout timeout() {
        return null;
      }

      @Override
      public Call clone() {
        return null;
      }
    });

    expectedException.expect(HttpClientException.class);
    expectedException.expectMessage("java.io.IOException: Deu erro no IO");

    when(okClient, "newCall", any(Request.class))
        .thenReturn(resp);

    HttpClient httpClient = spy(new HttpClient());

    when(httpClient, "buildClient").thenReturn(okClient);

    whenNew(HttpClient.class).withNoArguments().thenReturn(httpClient);
    ZenviaAPI.SMS(req).sendUnique();
  }

}