package com.javaedge.concurrency.example.deadLock;

import lombok.extern.slf4j.Slf4j;

/**
 * 一个简单的死锁类
 * 当DeadLock类的对象flag==1时（td1），先锁定o1,睡眠500毫秒
 * 而td1在睡眠的时候另一个flag==0的对象（td2）线程启动，先锁定o2,睡眠500毫秒
 * td1睡眠结束后需要锁定o2才能继续执行，而此时o2已被td2锁定；
 * td2睡眠结束后需要锁定o1才能继续执行，而此时o1已被td1锁定；
 * td1、td2相互等待，都需要得到对方锁定的资源才能继续执行，从而死锁。
 */

@Slf4j
public class DeadLock implements Runnable {
    public int flag = 1;
    //静态对象是类的所有对象共享的
    private static Object o1 = new Object(), o2 = new Object();

    @Override
    public void run() {
        log.info("flag:{}", flag);
        if (flag == 1) {
            synchronized (o1) {
                try {
                    Thread.sleep(500);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // 本质上是由于线程1,2并行执行，分别拿到对象1,2的锁，然后同时休眠500ms
                // 休眠结束后，同时开始想拿对方的锁，但是自己之前拿到的锁又没有被释放
                // 因此陷入了永远的死锁
                // synchronized 做同步的一个最大的缺点就是，无法主动释放锁，也不知道什么时候可以释放锁
                // synchronized 释放锁的情况：1）被锁住的代码块执行完成 ，2）被锁住的代码块发生了异常，JVM会让该线程自动释放锁
                // (前提是JVM正常运行，如果JVM被异常终止了进程或者服务器关机，则无法释放，但是这种情况下，
                // 自动重启JVM的话，这个锁也不会存在了)
                // 另外一个缺点是 synchronized 是非公平锁，且无法实现读读不冲突(也就是这种情况下 2个读线程都会冲突，远不如Lock 方便)
                synchronized (o2) {
                    log.info("1");
                }
            }
        }
        if (flag == 0) {
            synchronized (o2) {
                try {
                    Thread.sleep(500);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                synchronized (o1) {
                    log.info("0");
                }
            }
        }
    }

    public static void main(String[] args) {
        DeadLock td1 = new DeadLock();
        DeadLock td2 = new DeadLock();
        td1.flag = 1;
        td2.flag = 0;
        //td1,td2都处于可执行状态，但JVM线程调度先执行哪个线程是不确定的。
        //td2的run()可能在td1的run()之前运行
        new Thread(td1).start();
        new Thread(td2).start();
    }
}
