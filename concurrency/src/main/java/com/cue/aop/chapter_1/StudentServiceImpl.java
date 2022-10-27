package com.cue.aop.chapter_1;

public class StudentServiceImpl implements IStudentService {
    @Override
    public void add() {
        System.out.println("StudentServiceImpl 的add 方法");
    }
}
