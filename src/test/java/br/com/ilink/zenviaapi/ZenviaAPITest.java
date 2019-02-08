package br.com.ilink.zenviaapi;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.any;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import br.com.ilink.zenviaapi.ZenviaAPI.ReceivedMessage;
import br.com.ilink.zenviaapi.exceptions.HttpClientException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
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
import org.hamcrest.collection.IsEmptyCollection;
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
  ZenviaAPI.SMSRequest req;

  @Rule
  ExpectedException expectedException = ExpectedException.none();

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    req = ZenviaAPI.SMSRequest.builder()
        .para("5562996020305")
        .mensagem("Mensagem de teste")
        .codigoInterno("123456CALL")
        .build();
  }

  @Test
  public void testConsultaPorPeriodoDeveSerVazia() throws IOException {
    mockStatic(HttpClient.class);
    when(HttpClient.GET(any(), any(), any()))
        .thenReturn("{\n"
            + "    \"receivedResponse\": {\n"
            + "        \"statusCode\": \"00\",\n"
            + "        \"statusDescription\": \"Ok\",\n"
            + "        \"detailCode\": \"301\",\n"
            + "        \"detailDescription\": \"No received messages found\",\n"
            + "        \"receivedMessages\": null\n"
            + "    }\n"
            + "}");

    List<ZenviaAPI.ReceivedMessage> resp = ZenviaAPI
        .preparar(LocalDateTime.now().minusMinutes(5), LocalDateTime.now())
        .check();
    assertThat(resp, is(IsEmptyCollection.empty()));
  }

  @Test
  public void testConsultaPorPeriodoDeveTerUm() throws IOException {
    mockStatic(HttpClient.class);
    when(HttpClient.GET(any(), any(), any()))
        .thenReturn("{\n"
            + "    \"receivedResponse\": {\n"
            + "        \"statusCode\": \"00\",\n"
            + "        \"statusDescription\": \"Ok\",\n"
            + "        \"detailCode\": \"301\",\n"
            + "        \"detailDescription\": \"No received messages found\",\n"
            + "        \"receivedMessages\":  [\n"
            + "          {\n"
            + "            \"id\": 23190501,\n"
            + "            \"dateReceived\": \"2014-08-22T14:49:36\",\n"
            + "            \"mobile\": \"5511999999999\",\n"
            + "            \"body\": \"Pare\",\n"
            + "            \"shortcode\": \"30133\",\n"
            + "            \"mobileOperatorName\": \"Claro\",\n"
            + "            \"mtId\": \"hs863223748\"\n"
            + "          },\n"
            + "          {\n"
            + "            \"id\": 4532,\n"
            + "            \"dateReceived\": \"2014-08-22T14:49:36\",\n"
            + "            \"mobile\": \"5511999999999\",\n"
            + "            \"body\": \"Pare\",\n"
            + "            \"shortcode\": \"30133\",\n"
            + "            \"mobileOperatorName\": \"Claro\",\n"
            + "            \"mtId\": \"hs863223748\"\n"
            + "          }\n"
            + "        ]"
            + "    }\n"
            + "}");

    List<ReceivedMessage> resp = ZenviaAPI
        .preparar(LocalDateTime.now().minusMinutes(5), LocalDateTime.now())
        .check();
    assertThat(resp, not(IsEmptyCollection.empty()));
    assertThat(resp, is(hasSize(2)));
  }

  @Test
  public void testarEnvioDeUnicoSMS() {
    mockStatic(HttpClient.class);
    when(HttpClient.POST(any(), any(), any()))
        .thenReturn("{"
            + "  \"sendSmsResponse\": {"
            + "    \"statusCode\": \"00\","
            + "    \"statusDescription\": \"Ok\","
            + "    \"detailCode\": \"000\","
            + "    \"detailDescription\": \"Message Sent\""
            + "  }"
            + "}");

    List<ZenviaAPI.SMSResponse> resps = ZenviaAPI.preparar(req).enviar();
    assertThat(resps, is(notNullValue()));
  }

  @Test
  public void testarExcecoesServicosUnknownHostException() throws Exception {
    mockStatic(HttpClient.class);
    doThrow(new HttpClientException(new UnknownHostException("https://nao-existe-isso")))
        .when(HttpClient.class, "POST", any(), any(), any());

    when(prop, "getProperty", "config.url")
        .thenReturn("https://nao-existe-isso");
    Whitebox.setInternalState(HttpClient.class, "prop", prop);

    expectedException.expect(HttpClientException.class);
    expectedException.expectMessage("java.net.UnknownHostException: https://nao-existe-isso");

    ZenviaAPI.preparar(req).enviar();
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
    ZenviaAPI.preparar(req).enviar();
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
    ZenviaAPI.preparar(req).enviar();
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
    ZenviaAPI.preparar(req).enviar();
  }

}