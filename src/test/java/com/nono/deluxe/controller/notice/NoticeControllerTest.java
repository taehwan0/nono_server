package com.nono.deluxe.controller.notice;

import com.nono.deluxe.domain.notice.NoticeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.repository.query.Param;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class NoticeControllerTest {

    @LocalServerPort
    private int port;

    private MockMvc mvc;

    @Autowired
    WebApplicationContext context;

    @Autowired
    private NoticeRepository noticeRepository;

    @BeforeEach
    public void before() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
    }

    @AfterEach
    public void after() {
        // 연결된 DB 지워짐. 테스트용 h2를 쓰던가 하는 방법이 필요할듯.
        // Too many connections 처리에 대한 대책 강구.
        noticeRepository.deleteAll();
    }

    @Test
    public void test() {
        System.out.println("hello");
    }


}