package br.com.ilink.zenviaapisoap;

public enum RestService {

  SendUniqueSMS("/send-sms"),
  SendMultipleSMS("/send-sms-multiple");

  public String path;

  RestService(String path) {
    this.path = path;
  }
}
