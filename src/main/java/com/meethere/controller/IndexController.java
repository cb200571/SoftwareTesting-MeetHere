// 首页

package com.meethere.controller;

import com.meethere.entity.Message;
import com.meethere.entity.News;
import com.meethere.entity.Venue;
import com.meethere.entity.vo.MessageVo;
import com.meethere.service.MessageService;
import com.meethere.service.MessageVoService;
import com.meethere.service.NewsService;
import com.meethere.service.VenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
//声明控制类的注解

@Controller
public class IndexController {
    @Autowired
    private NewsService newsService;// 新闻业务
    @Autowired
    private VenueService venueService;// 场馆业务接口，存放场馆操作的方法
    @Autowired
    private MessageVoService messageVoService;// 留言
    @Autowired
    private MessageService messageService;// 留言业务

    @GetMapping("/index")//当请求路径是 /index 时，使用 index() 方法来处理
    //Pageable:Spring Data 提供的分页查询接口
    public String index(Model model){
        //从0开始,每页10条,按 venueID 升序排序
        Pageable venue_pageable= PageRequest.of(0,5, Sort.by("venueID").ascending());
        Pageable news_pageable= PageRequest.of(0,5, Sort.by("time").descending());//降序
        Pageable message_pageable= PageRequest.of(0,5, Sort.by("time").descending());
// 查询数据库，获取数据
        List<Venue> venue_list=venueService.findAll(venue_pageable).getContent();
        /*
        venueService.findAll(venue_pageable)：返回Page<Venue>（包含数据 + 分页信息）
        .getContent();只提取数据部分
        */
        List<News> news_list= newsService.findAll(news_pageable).getContent();
        Page<Message> messages=messageService.findPassState(message_pageable);
        List<MessageVo> message_list=messageVoService.returnVo(messages.getContent());
//将数据存入 Model 对象，传递给前端页面进行展示
        //model.addAttribute("属性名", 数据值);
        model.addAttribute("user", null);
        model.addAttribute("news_list",news_list);
        model.addAttribute("venue_list",venue_list);
        model.addAttribute("message_list",message_list);
        // 返回首页
        return "index";
    }

//管理员首页
    @GetMapping("/admin_index")
    public String admin_index(Model model){
        return "admin/admin_index";
    }



//    @GetMapping("/user_edit")
//    public String user_edit(Model model){
//        return "admin/user_edit";
//    }
}
