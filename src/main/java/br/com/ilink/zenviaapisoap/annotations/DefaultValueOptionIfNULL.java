package br.com.ilink.zenviaapisoap.annotations;

import br.com.ilink.zenviaapisoap.models.enums.CallbackOptionEnum;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DefaultValueOptionIfNULL {

  CallbackOptionEnum value() default CallbackOptionEnum.NONE;
}
