package com.meethere;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication//标识启动类
//@SpringBootApplication 默认扫描当前类所在包及所有子包。
//启动类，类名通常以 Application 结尾
public class MeetHereApplication {

    public static void main(String[] args) {
        SpringApplication.run(MeetHereApplication.class, args);
    }
    //MeetHereApplication.class：告诉 Spring 哪个类是启动类

}
