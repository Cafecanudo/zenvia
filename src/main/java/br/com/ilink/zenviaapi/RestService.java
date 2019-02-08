package br.com.ilink.zenviaapi;

public enum RestService {

  SendUniqueSMS("/send-sms"),
  CheckSMS("/received/search");

  public String path;

  RestService(String path) {
    this.path = path;
  }

}
