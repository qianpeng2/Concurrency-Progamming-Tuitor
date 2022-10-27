package com.cue.aop.chapter_1;

public class AOPtest {

    public static void main(String[] args) {
        StudentProxy proxy = SpringContextUtil.getBean("studentProxy", StudentProxy.class);
        proxy.add();
    }
}
