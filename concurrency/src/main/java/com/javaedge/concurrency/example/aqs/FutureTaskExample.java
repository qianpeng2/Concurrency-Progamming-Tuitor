package com.javaedge.concurrency.example.aqs;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

@Slf4j
public class FutureTaskExample {

    /**
     * FutureTask：可取消的异步任务框架， 线程的执行通过 FutureTask 的方式，而不是递交到线程池中执行
     * 线程执行完毕，main也就完毕
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        FutureTask<String> futureTask = new FutureTask<>(() -> {
            log.info("do something in callable");
            Thread.sleep(5000);
            return "Done";
        });

        new Thread(futureTask).start();
        log.info("do something in main");
        Thread.sleep(1000);
        if (!futureTask.isDone()) {
            log.info("任务没有完成，准备强制取消");
            futureTask.cancel(true);
            log.info("已强制取消异步任务");
            log.info("是否真的取消了？{}", futureTask.isCancelled());
        } else {
            log.info("任务完成，无法取消");
        }
        ////主线程抛出的异常：在获取异步任务的结果的时候， java.util.concurrent.CancellationException
        // futureTask.get() 一旦计算完成，就不能再次计算后者重复取消
        String result = futureTask.get();//会阻塞main线程，因此这里肯定是任务完成后再执行后面的代码
//        futureTask.run();
        log.info("FutureTask result：{}", result);
    }
}
