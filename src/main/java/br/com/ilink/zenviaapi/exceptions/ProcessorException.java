package br.com.ilink.zenviaapi.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;

public class ProcessorException extends RuntimeException {

  public ProcessorException(JsonProcessingException e) {
    super(e);
  }
}
