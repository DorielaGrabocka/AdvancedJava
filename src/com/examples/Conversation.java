/*
 * Copyright (c)  4/12/2020. Author Doriela Grabocka. All rights reserved.
 */

package com.examples;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Conversation {
    public static void main(String[] args){
        MessagePool pool = new MessagePool();
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.execute(new Person("Jane", pool));
        executorService.execute(new Person("John", pool));

        executorService.shutdown();
    }
}

class MessagePool{
    private final String[] responses={"Hi!", "Hello!", "How are you?", "I am fine, thank you!"};
    private int index=0;


    public synchronized void saySomething(Person p) throws  InterruptedException{
        System.out.println(Thread.currentThread().getName()+": "+responses[index++]);
        while(p.responded()){
            wait();
        }
        p.setResponded(true);
        notifyAll();
    }
}

class Person implements Runnable{
    private String name;
    private final MessagePool responses;//={"Hi!", "Hello!", "How are you?", "I am fine, thank you!"};
    private boolean responded = false;
//    private int responseIndex=0;
//    private boolean responded=false;

    public Person(String name, MessagePool pool) {
        this.name = name;
        responses = pool;
        Thread.currentThread().setName(name);
    }

    public void setResponded(boolean responded) {
        this.responded = responded;
    }

    public boolean responded() {
        return responded;
    }

    @Override
    public void run(){
        try{
            Thread.sleep(3000);
            for(int i=0; i<2;i++){
                responses.saySomething(this);
            }
        }catch(InterruptedException e){

        }
    }

    @Override
    public String toString() {
        return "Name: " + name;
    }
}
