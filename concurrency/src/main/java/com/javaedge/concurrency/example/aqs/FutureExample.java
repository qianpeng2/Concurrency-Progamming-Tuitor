package com.javaedge.concurrency.example.aqs;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
public class FutureExample {

    static class MyCallable implements Callable<String> {

        @Override
        public String call() throws Exception {
            log.info("do something in callable");
            Thread.sleep(5000);
            log.info("do something in callable done");
            return "Done";
        }
    }

    public static void main(String[] args) throws Exception {
        // 创建这个线程池的时候，默认的 keepAliveTime 是60s，因此在这个线程执行完毕后
        // 线程池其实还是在工作的，60s后才会关闭线程池
        /**
         * 因为 Executors.newCachedThreadPool() 默认的 corePoolSize =0
         * 而这里的线程数为1,大于0，这里的 keepAliveTime 是线程池中空闲的线程等待任务的最长时间
         * 也就是如果没有后续的任务过来，空闲的线程最多等待60s，就被销毁，此时线程池中已经没有了线程
         * 线程池也就被关闭了，此时控制台结束
         */
        ExecutorService executorService = Executors.newCachedThreadPool();
        Future<String> future = executorService.submit(new MyCallable());
        log.info("do something in main");
//        Thread.sleep(1000);
        String result = future.get();
        log.info("Callable result：{}", result);
    }
}
