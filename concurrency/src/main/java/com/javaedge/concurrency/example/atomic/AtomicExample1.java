package com.javaedge.concurrency.example.atomic;

import com.javaedge.concurrency.annoations.ThreadSafe;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@ThreadSafe
public class AtomicExample1 {

    // 请求总数
    public static int clientTotal = 100;

    // 同时并发执行的线程数
    public static int threadTotal = 5;

    public static AtomicInteger count = new AtomicInteger(0);

    public static void main(String[] args) throws Exception {
        ExecutorService executorService = Executors.newCachedThreadPool();
        final Semaphore semaphore = new Semaphore(threadTotal);
        final CountDownLatch countDownLatch = new CountDownLatch(clientTotal);
        for (int i = 1; i <= clientTotal; i++) {
            final int threadNum = i;
            executorService.execute(() -> {
                try {
                    log.info("i={}尝试获取许可", threadNum);
                    // 允许5个线程同时做 原子+1并发操作，且是线程安全的
                    semaphore.acquire();
                    add(threadNum);
                    semaphore.release();
                    log.info("i={}释放许可成功", threadNum);
                } catch (Exception e) {
                    log.error("exception", e);
                }
                countDownLatch.countDown();
            });
        }
        log.info("main-等待clientTotal");
        countDownLatch.await();// main 线程要等待100个线程的自增操作完成再关闭线程池，并打印出来 共享变量 count 的值
        log.info("main-等待clientTotal结束");
        executorService.shutdown();
        log.info("count:{}", count.get());
    }

    private static void add(final int threadNum) {
        log.info("i={},AtomicInteger-incrementAndGet", threadNum);
        count.incrementAndGet();
        // count.getAndIncrement();
    }
}
