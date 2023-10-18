package dev.dnnr.threadsrequest;

public class ThreadExec {
  public static void main (String[] args){
    ThreadExample thread = new ThreadExample("NOME DA THREAD");
    thread.run();
  }
}