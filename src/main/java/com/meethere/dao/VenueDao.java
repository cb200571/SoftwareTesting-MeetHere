//DAO 层接口，负责和数据库 venue 表交互




package com.meethere.dao;

import com.meethere.entity.Venue;
import org.springframework.data.jpa.repository.JpaRepository;//导入 Spring Data JPA 提供的 JpaRepository 接口，核心父接口，提供了大量现成的方法
/*
// 继承后，你直接就能用这些方法：
venueDao.save(venue);           // 保存/更新
venueDao.findById(id);          // 根据ID查询
venueDao.findAll();             // 查询所有
venueDao.findAll(pageable);     // 分页查询
venueDao.deleteById(id);        // 根据ID删除
venueDao.count();               // 统计总数
venueDao.existsById(id);        // 判断是否存在
 */

import org.springframework.data.jpa.repository.Query;//导入 @Query 注解，用于编写自定义的 SQL 或 JPQL 语句



import java.util.List;
//继承 Spring Data JPA 提供的通用 CRUD + 分页 + 排序能力，不用写实现类
//<Venue, Integer>：
// Venue：对应数据库表的实体类
//Integer：主键 venueID 的类型
public interface VenueDao extends JpaRepository<Venue, Integer> {
    Venue findByVenueID(int venueID);
    //Spring Data JPA 会根据方法名自动生成 SQL：-- 自动生成的 SQL
    //SELECT * FROM venue WHERE venue_id = ?

    Venue findByVenueName(String venueName);

    @Override//重写注解，重写父接口（JpaRepository）中的 findAll() 方法
    @Query(value = "select * from venue",nativeQuery = true)//自定义查询注解
    //nativeQuery = true声明这是原生 SQL（不是 JPQL）
    List<Venue> findAll();

    int countByVenueName(String venueName);

}
