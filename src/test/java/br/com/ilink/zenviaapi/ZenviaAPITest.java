package br.com.ilink.zenviaapi;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;

import br.com.ilink.zenviaapi.exceptions.HttpClientException;
import br.com.ilink.zenviaapi.models.SMSRequestModel;
import br.com.ilink.zenviaapi.models.SMSResponseModel;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"javax.net.ssl.*", "javax.security.*"})
@PrepareForTest({HttpClient.class, TrustConnection.class})
public class ZenviaAPITest {

  @Mock
  private Properties prop;
  private SMSRequestModel req;

  @Rule
  private ExpectedException expectedException = ExpectedException.none();

  @Before
  public void setUp() {
    req = SMSRequestModel.builder()
        .to(randomNumeric(12))
        .msg(randomAlphabetic(50))
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
    OkHttpClient cli = new Builder()
        .connectTimeout(1L, TimeUnit.MILLISECONDS)
        //.callTimeout(1L, TimeUnit.SECONDS)
        .build();

//    mockStatic(HttpClient.class);

    TrustConnection mock = PowerMockito.mock(TrustConnection.class);
    when(mock.buildClient())
        .thenReturn(cli);


//    expectedException.expect(HttpClientException.class);
//    expectedException.expectMessage("java.net.UnknownHostException: https://nao-existe-isso");

    ZenviaAPI.SMS(req).sendUnique();
  }

}