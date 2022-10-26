package com.javaedge.concurrency.example.aqs;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public class CountDownLatchExample2 {

    private final static int threadCount = 200;

    public static void main(String[] args) throws Exception {

        ExecutorService exec = Executors.newCachedThreadPool();

        final CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        log.info("开始...");
        for (int i = 1; i <= threadCount; i++) {
            final int threadNum = i;
            exec.execute(() -> {
                try {
                    test(threadNum);
                } catch (Exception e) {
                    log.error("exception", e);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        // 主线程执行到这里的时候等待，等待 countDownLatch 中的 count减为0，再执行main线程(main线程会被唤醒)，
        // 这中间，其他的线程池中的线程并发执行
        // 相比第一个示例，这里多了个等待超时时间，这个时间是最大的超时等待时间，如果超过了这个时间
        // 即便其他的线程池的线程没有执行完，这里也会继续执行下去
        // 因为每个线程池的线程的执行时间为100ms，只要这里的超时等待时间+主线程的代码执行时间<100ms，就可以保证先执行完main中的 log.info("finish");
        // 如果await 的等待时间超过100ms，比如设置为200，那么肯定是在各个线程并发之后才执行
        countDownLatch.await(1, TimeUnit.MILLISECONDS);// 其实就是达到了这个时间，那么锁释放，这里开始执行main线程代码
        log.info("finish");
        log.info("end");
        exec.shutdown();
    }

    private static void test(int threadNum) throws Exception {
        Thread.sleep(100);
        log.info("{}", threadNum);
    }
}
