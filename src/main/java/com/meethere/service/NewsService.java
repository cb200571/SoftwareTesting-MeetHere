//这是一个 Service 层接口，专门给 Controller 调用，用来处理新闻（News）相关业务。
//接口不能直接用，必须写 Impl 实现类
package com.meethere.service;
import com.meethere.entity.News;// 导入新闻实体类
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable
        ;
import java.util.List;

public interface NewsService {
    Page<News> findAll(Pageable pageable); // 分页查询所有新闻
//Page<News> 是返回值类型
// 返回新闻列表
//总记录数
//总页数
//当前页码
//每页大小
//是否有上一页
//是否有下一页
    News findById(int newsID); // 根据新闻ID查询一条新闻

    int create(News news); // 新增新闻

    void delById(int newsID);// 根据ID删除新闻

    void update(News news);// 修改新闻
}
