package com.javaedge.concurrency.example.lock;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Slf4j
public class LockExample3 {

    private final Map<String, Data> map = new TreeMap<>();

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private final Lock readLock = lock.readLock();

    private final Lock writeLock = lock.writeLock();

    public Data get(String key) {
        readLock.lock();
        try {
            return map.get(key);
        } finally {
            readLock.unlock();
        }
    }

    public Set<String> getAllKeys() {
        readLock.lock();
        try {
            return map.keySet();
        } finally {
            readLock.unlock();
        }
    }

    public Data put(String key, Data value) {
        writeLock.lock();
        try {
            return map.put(key, value);
        } finally {
            log.info("准备释放写锁");
            writeLock.unlock();// 妈的，这里写错了，是写锁的释放而不是读锁的释放
            log.info("写锁释放成功");//问题是这里的写锁无法释放
        }
    }

    static class Data {

    }

    /**
     * 测试读写锁
     *
     * @param args
     */
    public static void main(String[] args) throws InterruptedException {

        // 改了之后这个主线程居然不会停止
        ExecutorService executorService = Executors.newCachedThreadPool();
        final Semaphore semaphore = new Semaphore(5);
        int count = 10;// 在10个线程并发的情况下，正常，一旦线程数超过了100，那么还是容易出现并发的报错
        CountDownLatch countDownLatch = new CountDownLatch(count);
        LockExample3 lockExample3 = new LockExample3();
        for (int i = 0; i < count; i++) {
            final String num = String.valueOf(i);
            executorService.submit(() -> {// 用 submit 不会报错，但是用 execute 会报错
                try {
                    /**
                     * //如果没有的话，那么报错为: java.lang.IllegalMonitorStateException:
                     * attempt to unlock read lock, not locked by current thread
                     *
                     * 就是同时做读写操作的时候，很容易出现问题，如果是只读则没关系
                     */
                    log.info("线程{}尝试获取许可", num);
                    semaphore.acquire();
                    log.info("线程{}-put", num);
                    lockExample3.put(num, new Data());
                    log.info("getAllKeys={}", lockExample3.getAllKeys());
                    log.info("线程{}-get", num);
                    lockExample3.get(num);
                    semaphore.release();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            countDownLatch.countDown();
        }
        countDownLatch.await();
        log.info("getCount={}", countDownLatch.getCount());
        Thread.sleep(3000);
        executorService.shutdown();
        log.info("isShutdown={}", executorService.isShutdown());// true
        log.info("所有的key={}", lockExample3.getAllKeys());

    }
}
