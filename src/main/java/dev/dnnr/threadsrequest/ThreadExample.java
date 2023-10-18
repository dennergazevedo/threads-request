package dev.dnnr.threadsrequest;

public class ThreadExample extends Thread{
  
  private String name;

  public ThreadExample(String name){
    this.name = name;
  }

  public void run(){
    System.out.println("Executando thread " + name);
  }
}