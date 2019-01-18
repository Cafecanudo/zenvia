package br.com.ilink.zenviaapi;

import br.com.ilink.zenviaapi.models.SMSRequestModel;
import br.com.ilink.zenviaapi.models.SMSResponse;

public class ZenviaAPI extends RequestServer {

  private SMSRequestModel req;

  private ZenviaAPI(SMSRequestModel req) {
    this.req = req;
  }

  public static ZenviaAPI SMS(SMSRequestModel req) {
    return new ZenviaAPI(req);
  }

  public SMSResponse enviar() {
    String data = this.POST(req);
    return null;
  }


}
