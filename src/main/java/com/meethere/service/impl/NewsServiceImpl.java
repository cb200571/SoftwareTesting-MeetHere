package com.meethere.service.impl;

import com.meethere.dao.NewsDao;//注入 DAO（操作数据库的工具）
import com.meethere.entity.News;// 新闻实体类，映射数据库表
import com.meethere.service.NewsService;// 本类要实现的业务接口
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
//implements：必须重写接口中所有抽象方法
@Service
public class NewsServiceImpl implements NewsService {
    @Autowired//不用手动new对象
    private NewsDao newsDao;// 声明数据操作对象，用来读写数据库
    // 返回分页新闻数据，入参携带页码、条数、排序规则
    @Override//重写父类或实现接口用
    public Page<News> findAll(Pageable pageable) {
        return newsDao.findAll(pageable);
    }
    // 传入新闻ID，返回匹配的新闻实体对象
    @Override
    public News findById(int newsID) {
        return newsDao.getOne(newsID);
    }

    @Override
    public int create(News news) {
        return newsDao.save(news).getNewsID();
    }
    // 根据传入ID，删除对应新闻数据，无返回值
    @Override
    public void delById(int newsID) {
        newsDao.deleteById(newsID);
    }

    @Override
    public void update(News news) {
        newsDao.save(news);
    }
}
