package com.javaedge.concurrency.example.aqs;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

@Slf4j
public class SemaphoreExample2 {

    private final static int threadCount = 20;

    public static void main(String[] args) throws Exception {

        ExecutorService exec = Executors.newCachedThreadPool();

        final Semaphore semaphore = new Semaphore(3);

        for (int i = 1; i <= threadCount; i++) {
            final int threadNum = i;
            exec.execute(() -> {
                try {
                    log.info("线程{}尝试一次性获取4个许可", threadNum);
                    // 如果每次尝试的许可数量超过了 初始许可数量，那么所有的线程都尝试获取这么多许可
                    // 20个线程 全部进入无限的等待
                    semaphore.acquire(4); // 获取多个许可
                    test(threadNum);
                    log.info("线程{}尝试一次性释放3个许可", threadNum);
                    semaphore.release(3); // 释放多个许可
                    log.info("线程{}一次性释放3个许可成功", threadNum);
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
