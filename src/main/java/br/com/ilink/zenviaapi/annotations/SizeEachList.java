package br.com.ilink.zenviaapi.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SizeEachList {

  int min() default 0;

  int max() default -1;

  String message() default "Quantidade mínimo {min} e máxima de {max} caracteres, informou [{cur}].";

}
