package dev.dnnr.threadsrequest;

public class ThreadRunnableExample implements Runnable{
  
  private String name;

  public ThreadRunnableExample(String name){
    this.name = name;
  }

  @Override
  public void run(){
    try{
      System.out.println("Executando thread " + name);
    }catch(Exception error){
      error.printStackTrace();
    }
  }
}