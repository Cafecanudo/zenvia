package br.com.ilink.zenviaapisoap.models;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import br.com.ilink.zenviaapisoap.exceptions.ValidationException;
import br.com.ilink.zenviaapisoap.models.enums.CallbackOptionEnum;
import java.time.LocalDateTime;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class SMSRequestModelTest {

  @Rule
  public ExpectedException exceptionGrabber = ExpectedException.none();

  @Test
  public void testFromMax() throws ValidationException {
    exceptionGrabber.expect(ValidationException.class);
    exceptionGrabber.expectMessage("(from) Quantidade máxima de 50 caracteres.");

    SMSRequestModel.builder()
        .from(randomAlphabetic(51)) //<<<<<<<<<<<<<<<<<<
        .to(randomNumeric(12))
        .schedule(LocalDateTime.now())
        .msg(randomAlphabetic(50))
        .callbackOption(CallbackOptionEnum.NONE)
        .id(randomAlphabetic(10))
        .aggregateId(randomNumeric(20))
        .flashSms(false)
        .build();
  }

  @Test
  public void testToMin12() throws ValidationException {
    exceptionGrabber.expect(ValidationException.class);
    exceptionGrabber
        .expectMessage("(to) Quantidade mínimo 12 e máxima de 13 caracteres, informou [2].");

    SMSRequestModel.builder()
        .from(randomAlphabetic(20))
        .to(randomNumeric(2)) //<<<<<<<<<<<<<<<<<<
        .schedule(LocalDateTime.now())
        .msg(randomAlphabetic(50))
        .callbackOption(CallbackOptionEnum.NONE)
        .id(randomAlphabetic(10))
        .aggregateId(randomNumeric(20))
        .flashSms(false)
        .build();
  }

  @Test
  public void testToMax13() throws ValidationException {
    exceptionGrabber.expect(ValidationException.class);
    exceptionGrabber
        .expectMessage("(to) Quantidade mínimo 12 e máxima de 13 caracteres, informou [20].");

    SMSRequestModel.builder()
        .from(randomAlphabetic(20))
        .to(randomNumeric(20)) //<<<<<<<<<<<<<<<<<<
        .schedule(LocalDateTime.now())
        .msg(randomAlphabetic(50))
        .callbackOption(CallbackOptionEnum.NONE)
        .id(randomAlphabetic(10))
        .aggregateId(randomNumeric(20))
        .flashSms(false)
        .build();
  }

  @Test
  public void testToVaziio() throws ValidationException {
    exceptionGrabber.expect(ValidationException.class);
    exceptionGrabber.expectMessage("Não pode ser vazio.");

    SMSRequestModel.builder()
        .from(randomAlphabetic(20))
        .to(null) //<<<<<<<<<<<<<<<<<<
        .schedule(LocalDateTime.now())
        .msg(randomAlphabetic(50))
        .callbackOption(CallbackOptionEnum.NONE)
        .id(randomAlphabetic(10))
        .aggregateId(randomNumeric(20))
        .flashSms(false)
        .build();
  }

  @Test
  public void testToSomenteNumero() throws ValidationException {
    exceptionGrabber.expect(ValidationException.class);
    exceptionGrabber.expectMessage("(to) Somente números.");

    SMSRequestModel.builder()
        .from(randomAlphabetic(20))
        .to(randomAlphanumeric(12)) //<<<<<<<<<<<<<<<<<<
        .schedule(LocalDateTime.now())
        .msg(randomAlphabetic(50))
        .callbackOption(CallbackOptionEnum.NONE)
        .id(randomAlphabetic(10))
        .aggregateId(randomNumeric(20))
        .flashSms(false)
        .build();
  }

  @Test
  public void testScheduleNotNull() throws ValidationException {
    SMSRequestModel req = SMSRequestModel.builder()
        .from(randomAlphabetic(20))
        .to(randomNumeric(12))
        .schedule(null) //<<<<<<<<<<<<<<<<<<
        .msg(randomAlphabetic(50))
        .callbackOption(CallbackOptionEnum.NONE)
        .id(randomAlphabetic(10))
        .aggregateId(randomNumeric(20))
        .flashSms(false)
        .build();

    assertThat(req.getSchedule(), is(notNullValue()));
  }

  @Test
  public void testMsgMax() throws ValidationException {
    exceptionGrabber.expect(ValidationException.class);
    exceptionGrabber.expectMessage("(msg) Quantidade máxima de 152 caracteres.");

    SMSRequestModel.builder()
        .from(randomAlphabetic(20))
        .to(randomNumeric(12))
        .schedule(LocalDateTime.now())
        .msg(randomAlphabetic(153)) //<<<<<<<<<<<<<<<<<<
        .callbackOption(CallbackOptionEnum.NONE)
        .id(randomAlphabetic(10))
        .aggregateId(randomNumeric(20))
        .flashSms(false)
        .build();

  }

  @Test
  public void testCallbackOptionIfNull() throws ValidationException {
    SMSRequestModel req = SMSRequestModel.builder()
        .from(randomAlphabetic(20))
        .to(randomNumeric(12))
        .schedule(LocalDateTime.now())
        .msg(randomAlphabetic(50))
        .callbackOption(null)//<<<<<<<<<<<<<<<<<<
        .id(randomAlphabetic(10))
        .aggregateId(randomNumeric(20))
        .flashSms(false)
        .build();

    assertThat(req.getCallbackOption(), is(CallbackOptionEnum.NONE));
  }

  @Test
  public void testIDMax() throws ValidationException {
    exceptionGrabber.expect(ValidationException.class);
    exceptionGrabber.expectMessage("(id) Quantidade máxima de 15 caracteres.");

    SMSRequestModel.builder()
        .from(randomAlphabetic(20))
        .to(randomNumeric(12))
        .schedule(LocalDateTime.now())
        .msg(randomAlphabetic(151))
        .callbackOption(CallbackOptionEnum.NONE)
        .id(randomAlphabetic(40)) //<<<<<<<<<<<<<<<<<<
        .aggregateId(randomNumeric(20))
        .flashSms(false)
        .build();

  }

  @Test
  public void testAggregateIdMax() throws ValidationException {
    exceptionGrabber.expect(ValidationException.class);
    exceptionGrabber.expectMessage(
        "(aggregateId) Quantidade mínimo 0 e máxima de 50 caracteres, informou [200].");

    SMSRequestModel.builder()
        .from(randomAlphabetic(20))
        .to(randomNumeric(12))
        .schedule(LocalDateTime.now())
        .msg(randomAlphabetic(151))
        .callbackOption(CallbackOptionEnum.NONE)
        .id(randomAlphabetic(15))
        .aggregateId(randomNumeric(200))  //<<<<<<<<<<<<<<<<<<
        .flashSms(false)
        .build();
  }

  @Test
  public void testAggregateSomenteNumero() throws ValidationException {
    exceptionGrabber.expect(ValidationException.class);
    exceptionGrabber.expectMessage("(aggregateId) Somente números.");

    SMSRequestModel.builder()
        .from(randomAlphabetic(20))
        .to(randomNumeric(12))
        .schedule(LocalDateTime.now())
        .msg(randomAlphabetic(151))
        .callbackOption(CallbackOptionEnum.NONE)
        .id(randomAlphabetic(15))
        .aggregateId(randomAlphanumeric(10))  //<<<<<<<<<<<<<<<<<<
        .flashSms(false)
        .build();
  }

  @Test
  public void testFlhasFalseIfNull() throws ValidationException {
    SMSRequestModel req = SMSRequestModel.builder()
        .from(randomAlphabetic(20))
        .to(randomNumeric(12))
        .schedule(LocalDateTime.now())
        .msg(randomAlphabetic(151))
        .callbackOption(CallbackOptionEnum.NONE)
        .id(randomAlphabetic(15))
        .aggregateId(randomNumeric(10))
        //.flashSms(false) //<<<<<<<<<<<<<<<<<<
        .build();

    assertThat(req.getFlashSms(), is(false));
  }

}