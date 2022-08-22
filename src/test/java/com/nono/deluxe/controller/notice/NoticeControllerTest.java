package com.nono.deluxe.controller.notice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nono.deluxe.controller.notice.dto.CreateNoticeRequestDto;
import com.nono.deluxe.domain.notice.NoticeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class NoticeControllerTest {

    @LocalServerPort
    private int port;

    private MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

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

//    @AfterEach
//    public void after() {
//        // 연결된 DB 지워짐. 테스트용 h2를 쓰던가 하는 방법이 필요할듯.
//        // Too many connections 처리에 대한 대책 강구.
//        noticeRepository.deleteAll();
//    }

    @Test
    public void createNotice() throws Exception {
        //given
        CreateNoticeRequestDto createNoticeRequestDto = new CreateNoticeRequestDto(
                "title",
                "한글도 잘 들어가나요",
                true
        );
        String content = objectMapper.writeValueAsString(createNoticeRequestDto);

        //when
        mvc.perform(
                post("/notice", createNoticeRequestDto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andDo(print());

        //then

    }


}