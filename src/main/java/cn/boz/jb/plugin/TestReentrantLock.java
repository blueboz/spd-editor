package cn.boz.jb.plugin;

import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

public class TestReentrantLock {

    private ReentrantLock lock = new ReentrantLock();

    public void performTask() {
        lock.lock();
        try {
            synchronized (this){
                for (int i = 0; i < 3; i++) {
                    Thread.sleep(1000);
                    wait(1000);
                    LockSupport.parkNanos(10000000);
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        lock.unlock();
    }

    public static void main(String[] args) throws InterruptedException {
        TestReentrantLock testReentrantLock = new TestReentrantLock();
        Thread.sleep(6000);
        for (int i = 0; i < 5; i++) {
            Thread thread = new Thread(() -> {
                testReentrantLock.performTask();
            });

//            thread.interrupt();
            thread.start();
        }
    }
}
