package com.meethere.dao;

import com.meethere.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderDao extends JpaRepository<Order,Integer> {

    Order findByOrderID(int orderID);

    Page<Order> findAllByState(int state,Pageable pageable);

    List<Order> findByVenueIDAndStartTimeIsBetween(int venueID, LocalDateTime startTime, LocalDateTime startTime2);

    @Query(value = "select * from `order` o where o.state = ?1 or o.state = ?2 ", nativeQuery = true)
    List<Order> findAudit(int state1,int state2);

    Page<Order> findAllByUserID(String userID, Pageable pageable);

    @Transactional
    @Modifying
    @Query(value="update `order` o set o.state=?1 where o.orderID=?2",nativeQuery =true)
    void updateState(int state, int orderID);

    // 统计总订单数
    @Query(value = "select count(*) from `order`", nativeQuery = true)
    int countAllOrders();

    // 统计各状态订单数
    @Query(value = "select count(*) from `order` where state = ?1", nativeQuery = true)
    int countByState(int state);

    // 统计总营收（已完成的订单）
    @Query(value = "select IFNULL(sum(total),0) from `order` where state = 3", nativeQuery = true)
    int sumTotalRevenue();

    // 按月统计订单数（最近6个月）
    @Query(value = "select DATE_FORMAT(order_time,'%Y-%m') as month, count(*) as cnt from `order` group by month order by month desc limit 6", nativeQuery = true)
    List<Object[]> countOrdersByMonth();

    @Query(value = "select venueID, count(*) as cnt from `order` group by venueID order by cnt desc limit 5", nativeQuery= true)
    List<Object[]> findTopVenues();

    // ========== 以下为带筛选条件的统计方法 ==========

    // 带日期+状态+场馆筛选的订单总数（venueId=0时查询全部场馆）
    @Query(value = "select count(*) from `order` where order_time between :start and :end " +
            "AND (:state = 0 OR state = :state) AND (:venueId = 0 OR venueID = :venueId)", nativeQuery = true)
    int countOrdersFiltered(@Param("start") String start, @Param("end") String end,
                            @Param("state") int state, @Param("venueId") int venueId);
/*@Param("xxx")：绑定 SQL 里 :xxx 占位符；

 */
    // 带筛选的营收统计（已完成订单）
    @Query(value = "select IFNULL(sum(total),0) from `order` where state = 3 " +
            "AND order_time between :start and :end AND (:venueId = 0 OR venueID = :venueId)", nativeQuery = true)
    int sumRevenueFiltered(@Param("start") String start, @Param("end") String end, @Param("venueId") int venueId);

    // 带筛选的各状态订单数
    @Query(value = "select count(*) from `order` where state = :state " +
            "AND order_time between :start and :end AND (:venueId = 0 OR venueID = :venueId)", nativeQuery = true)
    int countByStateFiltered(@Param("state") int state, @Param("start") String start,
                             @Param("end") String end, @Param("venueId") int venueId);

    // 带筛选的按时间粒度分组统计
    @Query(value = "select DATE_FORMAT(order_time, :pattern) as period, count(*) as cnt " +
            "from `order` where order_time between :start and :end " +
            "AND (:state = 0 OR state = :state) AND (:venueId = 0 OR venueID = :venueId) " +
            "group by period order by period desc", nativeQuery = true)
    List<Object[]> countOrdersByPeriod(@Param("pattern") String pattern,
                                       @Param("start") String start, @Param("end") String end,
                                       @Param("state") int state, @Param("venueId") int venueId);

    // 带筛选的场馆排行
    @Query(value = "select venueID, count(*) as cnt from `order` " +
            "where order_time between :start and :end AND (:state = 0 OR state = :state) " +
            "group by venueID order by cnt desc limit 5", nativeQuery = true)
    List<Object[]> findTopVenuesFiltered(@Param("start") String start, @Param("end") String end,
                                         @Param("state") int state);

}
