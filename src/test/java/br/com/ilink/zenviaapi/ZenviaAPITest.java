package br.com.ilink.zenviaapi;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import br.com.ilink.zenviaapi.models.SMSRequestModel;
import br.com.ilink.zenviaapi.models.SMSResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"javax.net.ssl.*", "javax.security.*"})
public class ZenviaAPITest {

  private SMSRequestModel req;

  @Before
  public void setUp() {
    req = SMSRequestModel.builder()
        .to(randomNumeric(12))
        .msg(randomAlphabetic(50))
        .build();
  }

  @Test
  public void testEnvioSMS() {
    SMSResponse response = ZenviaAPI.SMS(req).enviar();
    assertThat(response, is(notNullValue()));
  }

}