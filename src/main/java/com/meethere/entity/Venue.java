package com.meethere.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.time.LocalTime;
import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Venue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int venueID;

    @Column(name="venue_name")//用于映射实体类字段与数据库表列之间的关系。"venue_name" 指定了数据库表中的列名
    private String venueName;//也可以不用@Column强制指定列名，JPA自动转换：遇到大写字母加下划线，然后全小写
    //JPA 是 Java 用来操作数据库的一套标准规范。三大核心
    //ORM 映射	Java 对象 ↔ 数据库表	@Entity、@Id、@Column
    //实体管理	增删改查操作	EntityManager、Repository
    //查询语言

    private String description;

    private int price;

    private String picture;

    private String address;

    private String open_time;//实际java不推荐下划线命名

    private String close_time;
}
