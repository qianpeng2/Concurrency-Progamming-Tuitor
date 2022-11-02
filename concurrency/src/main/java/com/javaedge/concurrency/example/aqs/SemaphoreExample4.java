package com.javaedge.concurrency.example.aqs;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

@Slf4j
public class SemaphoreExample4 {

    private final static int threadCount = 20;

    public static void main(String[] args) throws Exception {

        ExecutorService exec = Executors.newCachedThreadPool();

        final Semaphore semaphore = new Semaphore(3);

        for (int i = 1; i <= threadCount; i++) {
            final int threadNum = i;
            exec.execute(() -> {
                try {
                    log.info("i={}尝试获取一个许可", threadNum);
                    // 5s是等待许可的最大时间,超过时间后，返回false
                    if (semaphore.tryAcquire(5000, TimeUnit.MILLISECONDS)) { // 尝试获取一个许可
                        log.info("i={}成功获取一个许可", threadNum);
                        test(threadNum);
                        semaphore.release(); // 释放一个许可
                        log.info("i={}成功释放一个许可", threadNum);
                    }
                } catch (Exception e) {
                    log.error("exception", e);
                }
            });
        }
        exec.shutdown();
    }

    private static void test(int threadNum) throws Exception {
        log.info("{}", threadNum);
        // 如果这里的线程执行时间 > timeout ，那么也就是第一次3个线程可以获取到许可，然后释放许可
        // 后面的线程在准备获取许可的时候，已经等待了10s了，超过了指定的5s时间，因此返回false，不会执行test方法
        Thread.sleep(10000);
    }
}
