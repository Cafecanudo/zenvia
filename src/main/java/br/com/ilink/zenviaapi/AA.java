package br.com.ilink.zenviaapi;

public class AA {

  public static class A extends B{

    public static String builder(){
      return new A().get();
    }

    private String get() {
      return this.getServeName();
    }
  }

  public static abstract class B{

    protected String getServeName(){
      return "Server 1";
    }
  }

}
