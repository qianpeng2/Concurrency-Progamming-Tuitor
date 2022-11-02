package com.javaedge.concurrency.example.atomic;

import com.javaedge.concurrency.annoations.ThreadSafe;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@ThreadSafe
public class AtomicExample4 {

    // AtomicReference 不仅仅是封装Integer ，还是可以封装普通对象 Object
    private static AtomicReference<Integer> count = new AtomicReference<>(0);

    public static void main(String[] args) {
        // count 初始值为0，compareAndSet：如果期望值和实际值相等，那么更新为update值，否则不变
        count.compareAndSet(0, 2); // count=2
        count.compareAndSet(0, 1); // no
        count.compareAndSet(1, 3); // no
        count.compareAndSet(2, 4); // 4
        count.compareAndSet(3, 5); // no
        log.info("count:{}", count.get());
    }
}
