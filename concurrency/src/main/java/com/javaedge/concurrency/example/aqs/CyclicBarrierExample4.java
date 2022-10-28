package com.javaedge.concurrency.example.aqs;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public class CyclicBarrierExample4 {

    private static CyclicBarrier barrier = new CyclicBarrier(2);

    public static void main(String[] args) throws Exception {

        ExecutorService executor = Executors.newCachedThreadPool();
        log.info("main-线程for...");
        for (int i = 1; i <= 2; i++) {
            final int threadNum = i;
            Thread.sleep(2000);
            executor.execute(() -> {
                try {
                    log.info("线程-{} 递交", threadNum);
                    race(threadNum);
                } catch (Exception e) {
                    log.error("exception", e);
                }
            });
        }
        log.info("main-shutdown...");
        executor.shutdown();
    }

    /**
     * 验证 barrier.await 的超时等待时间是从第一个线程达到 栅栏点算起
     *
     * @param threadNum
     * @throws Exception
     */
    private static void race(int threadNum) throws Exception {
        Thread.sleep(1000);
        log.info("线程-{} 到达栅栏点", threadNum);
        barrier.await(1000, TimeUnit.MILLISECONDS);//此时第二个线程会无法按时达到栅栏点，抛出异常
        log.info("线程-{} 越过栅栏继续执行", threadNum);
    }
}
