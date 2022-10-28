package com.javaedge.concurrency.example.aqs;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class CyclicBarrierExample3 {

    private static CyclicBarrier barrier = new CyclicBarrier(5, () -> {
        // 在所有线程达到栅栏点的时候，也就是栅栏被打开的时候触发
        // 因为这个栅栏是可以被多次打开的，因此这里会打开栅栏2次，回调2次(线程10个/栅栏要求数5)
        // 如果线程池中的线程 i < 5 ,那么回调这里会一直阻塞，等待，并不是因为阻塞在回调这里
        // 而是栅栏处被阻塞了
        log.info("callback is running");
    });

    public static void main(String[] args) throws Exception {

        ExecutorService executor = Executors.newCachedThreadPool();
        log.info("开始执行主线程...");
        for (int i = 1; i <= 10; i++) {
            final int threadNum = i;
//            Thread.sleep(1000);//主线程的休眠
            log.info("{} ready execute", threadNum);
            executor.execute(() -> {
                try {
                    race(threadNum);
                } catch (Exception e) {
                    log.error("exception", e);
                }
            });
        }
        log.info("主线程main ready shutdown");
        executor.shutdown();
    }

    private static void race(int threadNum) throws Exception {
        Thread.sleep(100 * threadNum);
        log.info("{} is ready", threadNum);
        barrier.await();
        Thread.sleep(100 * threadNum);
        log.info("{} continue", threadNum);
    }
}
