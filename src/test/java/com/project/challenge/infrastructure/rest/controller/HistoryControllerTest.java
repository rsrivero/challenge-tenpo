package com.project.challenge.infrastructure.rest.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.challenge.application.adapter.HistoryCommandService;
import com.project.challenge.domain.entity.History;
import com.project.challenge.infrastructure.persistence.repository.HistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class HistoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private HistoryCommandService historyCommandService;

    @Mock
    private HistoryRepository historyRepository;

    private static final String HIST_GET_URL = "/history";

    private static final String SUM_POST_URL = "/operation/sum";

    @BeforeEach
    public void setup(){
        History history = new History();
        history.setMethod("get");
        history.setStatusCode(200);
        history.setResponse(objectMapper.convertValue("{\"message\":\"success\"}", JsonNode.class));
        history.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
        history.setUri(SUM_POST_URL);
        historyCommandService.save(history);
    }


    @Test
    public void test_GetHistory_Should_GetHistory_When_GetHistory() throws Exception {
        var request = prepareHistRequest(HIST_GET_URL, 0, 1);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.[0].id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.[0].method", containsString("get")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.[0].response", containsString("message")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.[0].response", containsString("success")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.[0].status_code").value(200));
    }

    private RequestBuilder prepareHistRequest(final String url, int page, int size) {
        return MockMvcRequestBuilders
                .get(url+"?page="+page+"&size="+size)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON_VALUE);
    }
}