package com.javaedge.concurrency;

import com.cue.aop.chapter_1.SpringContextUtil;
import com.cue.aop.chapter_1.StudentProxy;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ConcurrencyApplicationTests {

//    @Resource
//    private ApplicationContext applicationContext;
	/*@Test
	public void contextLoads() {
	}*/

    @Test
    public void testAOP() {

//        String name = applicationContext.getApplicationName();
//        System.out.println("name= " + name);
//        StudentProxy proxy = applicationContext.getBean("studentProxy", StudentProxy.class);
        StudentProxy proxy = SpringContextUtil.getBean("studentProxy", StudentProxy.class);
        proxy.add();
    }

}
