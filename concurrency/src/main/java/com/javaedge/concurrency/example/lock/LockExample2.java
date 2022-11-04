package com.javaedge.concurrency.example.lock;

import com.javaedge.concurrency.annoations.ThreadSafe;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@ThreadSafe
public class LockExample2 {

    // 请求总数
    public static int clientTotal = 5000;

    // 同时并发执行的线程数
    public static int threadTotal = 200;

    public static int count = 0;

    private final static Lock lock = new ReentrantLock();

    public static void main(String[] args) throws Exception {
        ExecutorService executorService = Executors.newCachedThreadPool();
        final Semaphore semaphore = new Semaphore(threadTotal);
        final CountDownLatch countDownLatch = new CountDownLatch(clientTotal);
        for (int i = 0; i < clientTotal; i++) {
            final int num = i;
            executorService.execute(() -> {
                try {
                    semaphore.acquire();
                    add(num);
                    semaphore.release();
                } catch (Exception e) {
                    log.error("exception", e);
                }
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        executorService.shutdown();
        log.info("count:{}", count);
    }

    private static void add(int num) {

        // 不能放在 try 中，否则多个线程同时到达，尝试加锁的时候
        // 加锁失败的线程也被迫进入了 finally方法，将这个锁释放掉(相当于释放掉了其他线程持有的锁)，这是严重的问题
//        lock.lock();

        try {
            lock.lock();// 加锁操作放到这里，且尝试抛出异常，结果也是正确的，为啥?
            // 加锁操作必须放到try 代码块，因为这样，就算异常，也可以捕获且释放锁
            count++;
            if (num / 10 == 0) {
                throw new RuntimeException("异常");
            }

        } finally {
            lock.unlock();
        }
    }
}
