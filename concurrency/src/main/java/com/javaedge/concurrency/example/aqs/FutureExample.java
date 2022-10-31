package com.javaedge.concurrency.example.aqs;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

@Slf4j
public class FutureExample {

    static class MyCallable implements Callable<String> {

        @Override
        public String call() throws Exception {
            log.info("do something in callable");
            Thread.sleep(1000);
            log.info("do something in callable done");
            throw new Exception("出错了");
//            return "Done";
        }
    }

    static class MyRunnable implements Runnable {

        /**
         * 重写的方法 不能向上抛出异常，只能try - catch,为啥？
         * 线程的启动是通过 Thread.start 方法执行的，线程的执行是 执行的run 方法
         * 如果 run 方法中抛出异常，相当于抛出到调用处，而它的调用方是JVM，JVM不会处理
         *
         */
        @Override
        public void run() {
            log.info("do something in Runnable");
            try {
                Thread.sleep(3000);
                log.info("不会执行");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("会执行");

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
//        executorService.submit(new MyRunnable());
        Future future2 = executorService.submit(new MyRunnable());
        log.info("do something in main");
//        Thread.sleep(1000);
        String result = null;
        try {
            result = future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            // 可以捕获到 Callable 执行中的异常，从而做出其他处理，比如多个线程如果一旦其中一个异常就全部退出
            e.printStackTrace();
//            executorService.shutdown();//碰到异常则退出(退出的时候，将其他已被递交的线程也执行完毕再退出)
            executorService.shutdownNow();//直接退出，而不管其他的被递交但是未执行完毕的线程
        }
        Object result2 = future2.get();//得到的会是一个 null
        log.info("Callable result：{}", result);
        log.info("Runnable result：{}", result2);
    }
}
