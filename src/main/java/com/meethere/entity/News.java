package com.meethere.entity;
//新闻实体类
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.tomcat.jni.Local;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@Entity //表明这个类是一个实体类，会与数据库中的一张表映射。
@NoArgsConstructor//无参构造器
@AllArgsConstructor//全参构造器
public class News {
    @Id//标记 newsID 是主键
    @GeneratedValue(strategy = GenerationType.IDENTITY)//让数据库【自动编号、自增】
    private int newsID;

    private String title;

    private String content;

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime time;
}
