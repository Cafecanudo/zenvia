package br.com.ilink.zenviaapisoap.utils;

import br.com.ilink.zenviaapisoap.annotations.DefaultBooleanIfNULL;
import br.com.ilink.zenviaapisoap.annotations.DefaultDateIfNULL;
import br.com.ilink.zenviaapisoap.annotations.DefaultValueOptionIfNULL;
import br.com.ilink.zenviaapisoap.annotations.NotBlank;
import br.com.ilink.zenviaapisoap.annotations.OnlyNumber;
import br.com.ilink.zenviaapisoap.annotations.Size;
import br.com.ilink.zenviaapisoap.exceptions.ValidationException;
import br.com.ilink.zenviaapisoap.models.enums.CallbackOptionEnum;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Arrays;

public abstract class BuilderValidation<M> {

  public abstract M build() throws ValidationException;

  protected final M validarParametros(M bean) throws ValidationException {
    Arrays.stream(bean.getClass().getDeclaredFields())
        .forEach(field -> Arrays.stream(field.getAnnotations()).forEach(annotation -> {
          validar(bean, getValue(bean, field.getName()), field, annotation);
        }));
    return bean;
  }

  private Object getValue(M bean, String fieldName) {
    try {
      Field field = getField(bean, fieldName);
      return field.get(bean);
    } catch (IllegalAccessException | NoSuchFieldException e) {
      throw new ValidationException(e);
    }
  }

  private void setValue(M bean, String fieldName, Object value) {
    try {
      Field field = getField(bean, fieldName);
      field.set(bean, value);
    } catch (IllegalAccessException | NoSuchFieldException e) {
      throw new ValidationException(e);
    }
  }

  private void validar(M bean, Object value, Field field, Annotation annotation) {
    //Setters
    if (annotation.annotationType().equals(DefaultBooleanIfNULL.class)) {
      if (value == null) {
        DefaultBooleanIfNULL ann = (DefaultBooleanIfNULL) annotation;
        setValue(bean, field.getName(), ann.value());
      }
    }
    if (annotation.annotationType().equals(DefaultValueOptionIfNULL.class)) {
      if (value == null) {
        DefaultValueOptionIfNULL ann = (DefaultValueOptionIfNULL) annotation;
        setValue(bean, field.getName(), ann.value() != null ? CallbackOptionEnum.NONE : ann);
      }
    }
    if (annotation.annotationType().equals(DefaultDateIfNULL.class)) {
      if (value == null) {
        setValue(bean, field.getName(), LocalDateTime.now());
      }
    }

    //Validadores
    if (annotation.annotationType().equals(NotBlank.class)) {
      validarVazio(value, field, (NotBlank) annotation);
    }
    if (annotation.annotationType().equals(Size.class)) {
      validarTamando((String) value, field, (Size) annotation);
    }
    if (annotation.annotationType().equals(OnlyNumber.class)) {
      validarSomenteNumeros((String) value, field, (OnlyNumber) annotation);
    }
  }

  private void validarSomenteNumeros(String value, Field field, OnlyNumber annotation) {
    OnlyNumber notBlank = annotation;
    if (value != null && !value.isEmpty() && !value.matches("[0-9]+")) {
      throw new ValidationException(field.getName(), notBlank.message());
    }
  }

  private void validarVazio(Object value, Field field, NotBlank annotation) {
    NotBlank notBlank = annotation;
    if (value == null
        || (field.getType().equals(String.class) && value.toString().isEmpty())) {
      throw new ValidationException(field.getName(), notBlank.message());
    }
  }

  private void validarTamando(String value, Field field, Size annotation) {
    Size size = annotation;
    String _value = value;
    if (_value != null && (_value.length() < size.min() || _value.length() > size.max())) {
      throw new ValidationException(field.getName(),
          size.message()
              .replaceAll("\\{cur\\}", String.valueOf(_value.length()))
              .replaceAll("\\{min\\}", String.valueOf(size.min()))
              .replaceAll("\\{max\\}", String.valueOf(size.max()))
      );
    }
  }

  private Field getField(M bean, String fieldName) throws NoSuchFieldException {
    Field field = bean.getClass().getDeclaredField(fieldName);
    field.setAccessible(true);
    return field;
  }
}
