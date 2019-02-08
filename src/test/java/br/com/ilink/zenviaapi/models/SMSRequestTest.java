package br.com.ilink.zenviaapi.models;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import br.com.ilink.zenviaapi.ZenviaAPI;
import br.com.ilink.zenviaapi.exceptions.ValidationException;
import br.com.ilink.zenviaapi.models.enums.CallbackOptionEnum;
import java.time.LocalDateTime;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class SMSRequestTest {

  @Rule
  public ExpectedException exceptionGrabber = ExpectedException.none();

  @Test
  public void testFromMax() throws ValidationException {
    exceptionGrabber.expect(ValidationException.class);
    exceptionGrabber.expectMessage("(de) Quantidade máxima de 50 caracteres.");

    ZenviaAPI.SMSRequest.builder()
        .de(randomAlphabetic(51)) //<<<<<<<<<<<<<<<<<<
        .para(randomNumeric(12))
        .agendado(LocalDateTime.now())
        .mensagem(randomAlphabetic(50))
        .callback(CallbackOptionEnum.NONE)
        .codigoInterno(randomAlphabetic(10))
        .codigoAgregado(randomNumeric(20))
        .smsUrgente(false)
        .build();
  }

  @Test
  public void testToMin12() throws ValidationException {
    exceptionGrabber.expect(ValidationException.class);
    exceptionGrabber
        .expectMessage("(para) Quantidade mínimo 12 e máxima de 13 caracteres, informou [2].");

    SMSRequest.builder()
        .de(randomAlphabetic(20))
        .para(randomNumeric(2)) //<<<<<<<<<<<<<<<<<<
        .agendado(LocalDateTime.now())
        .mensagem(randomAlphabetic(50))
        .callback(CallbackOptionEnum.NONE)
        .codigoInterno(randomAlphabetic(10))
        .codigoAgregado(randomNumeric(20))
        .smsUrgente(false)
        .build();
  }

  @Test
  public void testToMax13() throws ValidationException {
    exceptionGrabber.expect(ValidationException.class);
    exceptionGrabber
        .expectMessage("(para) Quantidade mínimo 12 e máxima de 13 caracteres, informou [20].");

    SMSRequest.builder()
        .de(randomAlphabetic(20))
        .para(randomNumeric(20)) //<<<<<<<<<<<<<<<<<<
        .agendado(LocalDateTime.now())
        .mensagem(randomAlphabetic(50))
        .callback(CallbackOptionEnum.NONE)
        .codigoInterno(randomAlphabetic(10))
        .codigoAgregado(randomNumeric(20))
        .smsUrgente(false)
        .build();
  }

  @Test
  public void testToVaziio() throws ValidationException {
    exceptionGrabber.expect(ValidationException.class);
    exceptionGrabber.expectMessage(
        "(para) Quantidade mínima da lista é 1 e máxima de [SEM LIMITES] itens, informou [0].");

    SMSRequest.builder()
        .de(randomAlphabetic(20))
        .agendado(LocalDateTime.now())
        .mensagem(randomAlphabetic(50))
        .callback(CallbackOptionEnum.NONE)
        .codigoInterno(randomAlphabetic(10))
        .codigoAgregado(randomNumeric(20))
        .smsUrgente(false)
        .build();
  }

  @Test
  public void testToSomenteNumero() throws ValidationException {
    exceptionGrabber.expect(ValidationException.class);
    exceptionGrabber.expectMessage("(para) Somente números.");

    SMSRequest.builder()
        .de(randomAlphabetic(20))
        .para(randomAlphanumeric(12)) //<<<<<<<<<<<<<<<<<<
        .agendado(LocalDateTime.now())
        .mensagem(randomAlphabetic(50))
        .callback(CallbackOptionEnum.NONE)
        .codigoInterno(randomAlphabetic(10))
        .codigoAgregado(randomNumeric(20))
        .smsUrgente(false)
        .build();
  }

  @Test
  public void testScheduleNotNull() throws ValidationException {
    SMSRequest req = SMSRequest.builder()
        .de(randomAlphabetic(20))
        .para(randomNumeric(12))
        .mensagem(randomAlphabetic(50))
        .callback(CallbackOptionEnum.NONE)
        .codigoInterno(randomAlphabetic(10))
        .codigoAgregado(randomNumeric(20))
        .smsUrgente(false)
        .build();

    assertThat(req.getAgendado(), is(notNullValue()));
  }

  @Test
  public void testMsgMax() throws ValidationException {
    exceptionGrabber.expect(ValidationException.class);
    exceptionGrabber.expectMessage("(mensagem) Quantidade máxima de 152 caracteres.");

    SMSRequest.builder()
        .de(randomAlphabetic(20))
        .para(randomNumeric(12))
        .agendado(LocalDateTime.now())
        .mensagem(randomAlphabetic(153)) //<<<<<<<<<<<<<<<<<<
        .callback(CallbackOptionEnum.NONE)
        .codigoInterno(randomAlphabetic(10))
        .codigoAgregado(randomNumeric(20))
        .smsUrgente(false)
        .build();

  }

  @Test
  public void testCallbackOptionIfNull() throws ValidationException {
    SMSRequest req = SMSRequest.builder()
        .de(randomAlphabetic(20))
        .para(randomNumeric(12))
        .agendado(LocalDateTime.now())
        .mensagem(randomAlphabetic(50))
        .callback(null)//<<<<<<<<<<<<<<<<<<
        .codigoInterno(randomAlphabetic(10))
        .codigoAgregado(randomNumeric(20))
        .smsUrgente(false)
        .build();

    assertThat(req.getCallback(), is(CallbackOptionEnum.NONE));
  }

  @Test
  public void testIDMax() throws ValidationException {
    exceptionGrabber.expect(ValidationException.class);
    exceptionGrabber.expectMessage("(codigoInterno) Quantidade máxima de 15 caracteres.");

    SMSRequest.builder()
        .de(randomAlphabetic(20))
        .para(randomNumeric(12))
        .agendado(LocalDateTime.now())
        .mensagem(randomAlphabetic(151))
        .callback(CallbackOptionEnum.NONE)
        .codigoInterno(randomAlphabetic(40)) //<<<<<<<<<<<<<<<<<<
        .codigoAgregado(randomNumeric(20))
        .smsUrgente(false)
        .build();

  }

  @Test
  public void testAggregateIdMax() throws ValidationException {
    exceptionGrabber.expect(ValidationException.class);
    exceptionGrabber.expectMessage(
        "(codigoAgregado) Quantidade mínimo 0 e máxima de 50 caracteres, informou [200].");

    SMSRequest.builder()
        .de(randomAlphabetic(20))
        .para(randomNumeric(12))
        .agendado(LocalDateTime.now())
        .mensagem(randomAlphabetic(151))
        .callback(CallbackOptionEnum.NONE)
        .codigoInterno(randomAlphabetic(15))
        .codigoAgregado(randomNumeric(200))  //<<<<<<<<<<<<<<<<<<
        .smsUrgente(false)
        .build();
  }

  @Test
  public void testAggregateSomenteNumero() throws ValidationException {
    exceptionGrabber.expect(ValidationException.class);
    exceptionGrabber.expectMessage("(codigoAgregado) Somente números.");

    SMSRequest.builder()
        .de(randomAlphabetic(20))
        .para(randomNumeric(12))
        .agendado(LocalDateTime.now())
        .mensagem(randomAlphabetic(151))
        .callback(CallbackOptionEnum.NONE)
        .codigoInterno(randomAlphabetic(15))
        .codigoAgregado(randomAlphanumeric(10))  //<<<<<<<<<<<<<<<<<<
        .smsUrgente(false)
        .build();
  }

  @Test
  public void testFlhasFalseIfNull() throws ValidationException {
    SMSRequest req = SMSRequest.builder()
        .de(randomAlphabetic(20))
        .para(randomNumeric(12))
        .agendado(LocalDateTime.now())
        .mensagem(randomAlphabetic(151))
        .callback(CallbackOptionEnum.NONE)
        .codigoInterno(randomAlphabetic(15))
        .codigoAgregado(randomNumeric(10))
        .build();

    assertThat(req.getSmsUrgente(), is(false));
  }

}