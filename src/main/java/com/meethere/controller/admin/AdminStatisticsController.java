package com.meethere.controller.admin;
import com.meethere.dao.OrderDao;
import com.meethere.dao.UserDao;
import com.meethere.dao.VenueDao;
import com.meethere.entity.Venue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class AdminStatisticsController {
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private VenueDao venueDao;

    // 1. 返回统计页面
    @RequestMapping("/statistics_manage")
    public String statistics() {
        return "admin/statistics";
    }

    // 2. 返回概览数据（JSON），支持筛选
    @GetMapping("/statistics/overview.do")
    @ResponseBody
    public Map<String, Object> getOverview(
            @RequestParam(defaultValue = "1970-01-01") String startDate,
            @RequestParam(defaultValue = "2099-12-31") String endDate,
            @RequestParam(defaultValue = "0") int venueId,
            @RequestParam(defaultValue = "0") int state) {
        Map<String, Object> map = new HashMap<>();

        // venueId=0时查询全部场馆，state=0时查询全部状态
        map.put("totalOrders", orderDao.countOrdersFiltered(startDate, endDate, state, venueId));
        map.put("totalRevenue", orderDao.sumRevenueFiltered(startDate, endDate, venueId));
        map.put("totalUsers", userDao.countAllUsers());
        map.put("totalVenues", venueDao.count());
        map.put("pendingOrders", orderDao.countByStateFiltered(1, startDate, endDate, venueId));
        map.put("approvedOrders", orderDao.countByStateFiltered(2, startDate, endDate, venueId));
        map.put("finishedOrders", orderDao.countByStateFiltered(3, startDate, endDate, venueId));
        map.put("rejectedOrders", orderDao.countByStateFiltered(4, startDate, endDate, venueId));
        return map;
    }

    // 3. 返回时间趋势（JSON），支持筛选 + 粒度切换
    @GetMapping("/statistics/monthlyOrders.do")
    @ResponseBody
    public List<Map<String, Object>> getMonthlyOrders(
            @RequestParam(defaultValue = "1970-01-01") String startDate,
            @RequestParam(defaultValue = "2099-12-31") String endDate,
            @RequestParam(defaultValue = "0") int venueId,
            @RequestParam(defaultValue = "0") int state,
            @RequestParam(defaultValue = "month") String granularity) {

        // 根据粒度选择 DATE_FORMAT 模式
        String pattern;
        switch (granularity) {
            case "day":
                pattern = "%Y-%m-%d";
                break;
            case "week":
                pattern = "%x-%v"; // ISO year-week
                break;
            default:
                pattern = "%Y-%m";
                break;
        }

        List<Object[]> rows = orderDao.countOrdersByPeriod(pattern, startDate, endDate, state, venueId);
        List<Map<String, Object>> list = new ArrayList<>();
        for (Object[] row : rows) {
            Map<String, Object> map = new HashMap<>();
            map.put("month", row[0]);
            map.put("count", row[1]);
            list.add(map);
        }
        return list;
    }

    // 4. 返回场馆订单排行（JSON），支持筛选
    @GetMapping("/statistics/venueRank.do")
    @ResponseBody
    public List<Map<String, Object>> getVenueRank(
            @RequestParam(defaultValue = "1970-01-01") String startDate,
            @RequestParam(defaultValue = "2099-12-31") String endDate,
            @RequestParam(defaultValue = "0") int state) {

        List<Object[]> rows = orderDao.findTopVenuesFiltered(startDate, endDate, state);
        List<Map<String, Object>> list = new ArrayList<>();
        for (Object[] row : rows) {
            Map<String, Object> map = new HashMap<>();
            Venue v = venueDao.getOne((int) row[0]);
            map.put("name", v.getVenueName());
            map.put("count", row[1]);
            list.add(map);
        }
        return list;
    }

    // 5. 返回场馆列表（用于下拉框）
    @GetMapping("/statistics/venueList.do")
    @ResponseBody
    public List<Map<String, Object>> getVenueList() {
        List<Venue> venues = venueDao.findAll();
        /* [
        Venue{id=1, venueName="一号场馆", address="一楼大厅", status=1},
        Venue{id=2, venueName="二号场馆", address="二楼会议室", status=1},
        Venue{id=3, venueName="三号场馆", address="三楼报告厅", status=0}
        传给前端：[
  {
    "id": 1,
    "venueName": "一号场馆",
    "address": "一楼大厅",
    "status": 1
  },
  {
    "id": 2,
    "venueName": "二号场馆",
    "address": "二楼会议室",
    "status": 1
  }
]
        */
        List<Map<String, Object>> list = new ArrayList<>();
        for (Venue v : venues) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", v.getVenueID());
            map.put("name", v.getVenueName());
            list.add(map);
        }
        return list;
    }
}
