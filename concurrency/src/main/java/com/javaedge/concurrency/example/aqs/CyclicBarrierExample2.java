package com.javaedge.concurrency.example.aqs;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public class CyclicBarrierExample2 {

    private static CyclicBarrier barrier = new CyclicBarrier(5);

    public static void main(String[] args) throws Exception {

        ExecutorService executor = Executors.newCachedThreadPool();

        log.info("开始执行线程...");
        for (int i = 1; i <= 10; i++) {
            final int threadNum = i;
//            Thread.sleep(1000);
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

    /**
     * 从打印的日志来看，存在有几个线程先 continue (此时说明栅栏已经打开)，但是后面再打印 ready，
     * 问题是日志系统无法精确到 毫秒，只能精确到10ms级别。造成日志不是严格按照时间顺序打印的
     *
     * @param threadNum
     * @throws Exception
     */
    private static void race(int threadNum) throws Exception {
        if (threadNum == 1) {
            Thread.sleep(3000);
        } else {
            Thread.sleep(1000);
        }
        log.info("{} is ready", threadNum);
        try {
            // 如果所有的线程都达到了栅栏处，或者指定的等待时间已过(此时会抛出 BrokenBarrierException )，栅栏就打开
            // 如果没有达到指定的 parties ，那么就一直等待到永远
            // barrier.await被调用的时候，说明这个线程已经到达了屏障点，如果没有超时的设置，且当前线程不是最后一个线程，那么会一直被阻塞等待
            /**
             * 这里的超时等待时间：指的是当第一个线程达到栅栏点的时候(而不是线程开始执行的时候)，会等待 1000ms，等待其他线程的到来
             * 如果其他线程没有在规定的时间内到达，那么抛出 异常-BrokenBarrierException
             * 此时栅栏点同样会打开，各个线程继续执行
             */
            barrier.await(1000, TimeUnit.MILLISECONDS);//线程每调一次，parties +1 ，直到加到初始值，所有的等待线程被释放，开始各自执行
        } catch (Exception e) {
            log.warn("BarrierException", e);
        }
        log.info("{} continue", threadNum);
    }
}
