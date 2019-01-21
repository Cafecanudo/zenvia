package br.com.ilink.zenviaapi.exceptions;

import java.io.IOException;

public class HttpClientException extends RuntimeException {

  public HttpClientException(IOException e) {
    super(e);
  }

  public HttpClientException() {
    super();
  }
}
