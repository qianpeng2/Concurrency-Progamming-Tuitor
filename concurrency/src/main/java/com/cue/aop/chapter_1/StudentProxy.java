package com.cue.aop.chapter_1;

import javax.annotation.Resource;

/**
 * 代理对象
 */
public class StudentProxy implements IStudentService {

    @Resource
    private StudentAOP studentAOP;//注入切面对戏

    @Resource
    private IStudentService studentService;//目标对象

    @Override
    public void add() {

        try {
            studentAOP.arounding();//执行环绕方法
            studentAOP.before();
            studentService.add();//执行目标对象方法
            studentAOP.after();
            studentAOP.afterReturning();
        } catch (Exception e) {
            studentAOP.afterThrowing();
        } finally {
            studentAOP.arounding();
        }
    }
}
