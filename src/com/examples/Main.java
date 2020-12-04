/*
 * Copyright (c) 2020. Author Doriela Grabocka. All rights reserved.
 */
package com.examples;

import java.security.SecureRandom;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) {
	// write your code here
        SynchronizedBuffer buffer = new SynchronizedBuffer();
        ExecutorService executorService = Executors.newCachedThreadPool();

        executorService.execute(new Producer(buffer));
        executorService.execute(new Consumer(buffer));
        executorService.shutdown();
    }
}

class SynchronizedBuffer{
    private int buffer=-1;
    private boolean occupied = false;

    public synchronized void blockingPut(int value) throws InterruptedException{
        while(occupied){
            System.out.printf("Producer tries to write!\n");
            displayState("Buffer is full");
            wait();
        }

        buffer = value;
        displayState(String.format("Producer is inserting value: %d",value));
        occupied = true;
        notifyAll();
    }

    public synchronized int blockingGet() throws InterruptedException{
        while(!occupied){
            System.out.printf("Buffer is empty\n");
            displayState("Consumer waiting for buffer to fill");
            wait();
        }

        int value = buffer;
        occupied = false;
        displayState("Consumer reads value: "+value);
        notifyAll();
        return value;

    }

    public synchronized void displayState(String message){
        System.out.printf(message+"\n");
    }

}

class Producer implements Runnable{
    private static final SecureRandom secureRandom = new SecureRandom();
    private final SynchronizedBuffer sharedLocation;

    public Producer(SynchronizedBuffer sharedLocation) {
        this.sharedLocation = sharedLocation;
    }

    @Override
    public void run() {
        int sum = 0;
        for (int i=1; i <= 10; i++){
            try{
                Thread.sleep(secureRandom.nextInt(3000));
                sharedLocation.blockingPut(i);
                sum+=i;
            }catch(InterruptedException e){
                System.out.printf("Producer was interrupted!\n");
            }
        }
        System.out.printf("Producer values totalling: "+sum+"\n");
    }
}

class Consumer implements Runnable{
    private static final SecureRandom secureRandom = new SecureRandom();
    private final SynchronizedBuffer sharedLocation;

    public Consumer(SynchronizedBuffer sharedLocation) {
        this.sharedLocation = sharedLocation;
    }

    @Override
    public void run(){
        int sum = 0;
        for (int i=1; i<=10; i++){
            try{
                Thread.sleep(secureRandom.nextInt(3000));
                sum+= sharedLocation.blockingGet();
            }catch(InterruptedException e){
                System.out.printf("Producer was interrupted!\n");
            }
        }
        System.out.printf("Consumers totalling sum is: "+sum+"\n");
    }
}
