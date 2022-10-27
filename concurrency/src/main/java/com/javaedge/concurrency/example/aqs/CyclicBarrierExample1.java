package com.javaedge.concurrency.example.aqs;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class CyclicBarrierExample1 {

    private static CyclicBarrier barrier = new CyclicBarrier(5);

    /**
     * CountDownLatch有几个问题：
     * 首先CountDownLatch在await之后必须依靠别的线程来给它countDown，打开门闩；
     * <p>
     * 其次CountDownLatch在countDown到0之后，该CountDownLatch的生命周期就结束了，它不能重用。
     * <p>
     * CyclicBarrier: 回环栅栏
     * 它可以自己给自己打开门闩(shuan) ，还能重用
     * 通俗的说，就是有一个栅栏-CyclicBarrier，需要n个人同时推倒，推倒后这n个人继续执行
     * 然后一旦栅栏被推倒，后面还可以再次竖起来
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        ExecutorService executor = Executors.newCachedThreadPool();

        for (int i = 0; i < 10; i++) {// 10，相当于栅栏打开2次，重用了一次，
            final int threadNum = i;
            Thread.sleep(1000);
            executor.execute(() -> {
                try {
                    race(threadNum);
                } catch (Exception e) {
                    log.error("exception", e);
                }
            });
        }
        executor.shutdown();
    }

    private static void race(int threadNum) throws Exception {
        Thread.sleep(1000);
        log.info("{} is ready", threadNum);
        barrier.await();
        log.info("{} continue", threadNum);
    }
}
