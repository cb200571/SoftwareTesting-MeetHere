package com.meethere.dao;
//数据访问层
import com.meethere.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsDao extends JpaRepository<News,Integer> {

}
