package com.tram.restapi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tram.restapi.common.ControllerTest;
import com.tram.restapi.domain.EventStatus;
import org.junit.Test;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class EventControllerTest extends ControllerTest {

    @Test
    public void create() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("안녕 이벤트")
                .description("배고프다")
                .beginEnrollmentDateTime(LocalDateTime.of(2018, 11, 2, 8, 0))
                .closeEnrollmentDateTime(LocalDateTime.of(2018, 11, 3, 8, 0))
                .beginEventDateTime(LocalDateTime.of(2018, 11, 4, 8, 0))
                .endEventDateTime(LocalDateTime.of(2018, 11, 5, 8, 0))
                .basePrice(0)
                .maxPrice(0)
                .location("네이버 D2 팩토리 좁았음")
                .limitOfEnrollment(100)
                .build();

        this.mockMvc.perform(post("/api/events")
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(this.objectMapper.writeValueAsString(eventDto)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(header().exists("Location"))
                    .andExpect(jsonPath("free").value(false))
                    .andExpect(jsonPath("id").exists())
                    .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()));
    }

    @Test
    public void createFailTestByValidAnnotation() throws Exception {
        EventDto eventDto = new EventDto();

        this.mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createFailTestByCustomEventDtoValidator() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("아침에 일어나기 너무 힘듬")
                .description("진짜")
                .beginEnrollmentDateTime(LocalDateTime.of(2018, 11, 5, 8, 0))
                .closeEnrollmentDateTime(LocalDateTime.of(2018, 11, 4, 8, 0))
                .beginEventDateTime(LocalDateTime.of(2018, 11, 3, 8, 0))
                .endEventDateTime(LocalDateTime.of(2018, 11, 2, 8, 0))
                .basePrice(1000)
                .maxPrice(500)
                .location("네이버 D2 팩토리")
                .limitOfEnrollment(100)
                .build();

        this.mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").hasJsonPath())
                .andExpect(jsonPath("$[0].rejectedValue").hasJsonPath())
                .andExpect(jsonPath("$[0].defaultMessage").hasJsonPath())
                .andExpect(jsonPath("$[0].objectName").hasJsonPath());
    }
}