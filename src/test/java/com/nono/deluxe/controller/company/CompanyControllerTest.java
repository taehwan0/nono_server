package com.nono.deluxe.controller.company;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.nono.deluxe.controller.company.dto.CreateCompanyRequestDto;
import com.nono.deluxe.domain.company.CompanyType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class CompanyControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("CreateNotice Validation Success Test")
    void controllerValidationSuccessTest() throws Exception {
        //given
        String name = "hello";
        CompanyType type = CompanyType.INPUT;
        String category = "world";

        //when
        CreateCompanyRequestDto requestDto = new CreateCompanyRequestDto(name, type, category);
        String jsonBody = objectMapper.writeValueAsString(requestDto);


        //then
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/company")
                        .header("Authorization", "hello,world")
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("CreateNotice Validation Fail Test")
    void controllerValidationFailTest() throws Exception {
        //given
        String name = " ";
        CompanyType type = CompanyType.INPUT;
        String category = "";

        //when
        CreateCompanyRequestDto requestDto = new CreateCompanyRequestDto(name, type, category);
        String jsonBody = objectMapper.writeValueAsString(requestDto);


        //then
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/company")
                        .header("Authorization", "hello,world")
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }
}
