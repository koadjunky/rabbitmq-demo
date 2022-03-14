package eu.malycha.rabbitmq.demo.audit;

import io.swagger.v3.oas.models.security.SecurityScheme;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuditController.class)
class AuditControllerTests {

    static final String ENDPOINT = "/api/counters";

    @Autowired
    MockMvc mvc;

    @MockBean
    TaskCounter taskCounter;

    @Test
    void getShouldReturnEmptyObjectWhenNoCountersCollected() throws Exception {
        when(taskCounter.getCounters()).thenReturn(new HashMap<>());
        mvc.perform(get(ENDPOINT).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getShouldReturnCountersIfCollected() throws Exception {
        when(taskCounter.getCounters()).thenReturn(Map.of("counter-1", 2, "counter-2", 1));
        mvc.perform(get(ENDPOINT).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.counter-1").value(2))
                .andExpect(jsonPath("$.counter-2").value(1));
    }

}
