package com.javaedge.concurrency.example.aqs;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

@Slf4j
public class SemaphoreExample1 {

    private final static int threadCount = 20;

    public static void main(String[] args) throws Exception {

        ExecutorService exec = Executors.newCachedThreadPool();

        // 信号量本质是一个计数器或者是共享锁，在申明阶段可以设置许可数量(也就是多少个线程可以同时被持有锁)
        // 每 release 一次，就释放一个许可，然后可以被其他的线程再次持有
        // 当 semaphore 上的许可数量为 0 的时候，调用处会被阻塞等待状态
        final Semaphore semaphore = new Semaphore(3);

        for (int i = 1; i <= threadCount; i++) {
            final int threadNum = i;
            exec.execute(() -> {
                try {
                    semaphore.acquire(); // 获取一个许可
                    test(threadNum);
                    semaphore.release(); // 释放一个许可
                } catch (Exception e) {
                    log.error("exception", e);
                }
            });
        }
        exec.shutdown();
    }

    private static void test(int threadNum) throws Exception {
        log.info("{}", threadNum);
        Thread.sleep(1000);
    }
}
