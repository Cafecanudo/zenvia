package br.com.ilink.zenviaapisoap.exceptions;

import java.io.IOException;

public class HttpClientException extends RuntimeException {

  public HttpClientException(IOException e) {
    super(e);
  }

  public HttpClientException() {
    super();
  }
}
