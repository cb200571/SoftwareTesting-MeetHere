package com.meethere.IntegrationTest.Controller.Admin;

import com.meethere.MeetHereApplication;
import com.meethere.entity.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.ModelAndViewAssert.assertModelAttributeAvailable;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = MeetHereApplication.class)
@AutoConfigureMockMvc
@Transactional
public class AdminOrderApiTest {
    @Autowired
    private MockMvc mockMvc;
    @Test
    public void return_reservation_manage_html() throws Exception {
        ResultActions perform=mockMvc.perform(get("/reservation_manage"));
        MvcResult mvcResult=perform.andReturn();
        ModelAndView mv=mvcResult.getModelAndView();
        perform.andExpect(status().isOk());
        assertModelAttributeAvailable(mv,"order_list");
        assertModelAttributeAvailable(mv,"total");
    }

    @Test
    public void admin_get_no_audit_order_list() throws Exception {
        ResultActions perform=mockMvc.perform(get("/admin/getOrderList.do").param("page","1"));
        perform.andExpect(status().isOk());

    }

    @Test
    public void admin_confirm_order() throws Exception {
        ResultActions perform=mockMvc.perform(post("/passOrder.do").param("orderID","1"));
        perform.andExpect(status().isOk());
    }

    @Test
    public void admin_reject_order() throws Exception {
        ResultActions perform=mockMvc.perform(post("/rejectOrder.do").param("orderID","1"));
        perform.andExpect(status().isOk());

    }
}
