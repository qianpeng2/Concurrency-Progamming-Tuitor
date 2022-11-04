package com.javaedge.concurrency.example.immutable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.javaedge.concurrency.annoations.ThreadSafe;

@ThreadSafe
public class ImmutableExample3 {

    // Immutable 就是不可变的意思，相关的类都是不可变类
    private final static ImmutableList<Integer> list = ImmutableList.of(1, 2, 3);// 不能再做更新操作了，否则异常

    private final static ImmutableSet set = ImmutableSet.copyOf(list);

    private final static ImmutableMap<Integer, Integer> map = ImmutableMap.of(1, 2, 3, 4);

    private final static ImmutableMap<Integer, Integer> map2 = ImmutableMap.<Integer, Integer>builder()
            .put(1, 2).put(3, 4).put(5, 6).build();


    public static void main(String[] args) {
        System.out.println(map2.get(3));
    }
}
