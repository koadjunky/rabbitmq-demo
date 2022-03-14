package eu.malycha.rabbitmq.demo.producer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProducerController.class)
class ProducerControllerTests {

    static final String ENDPOINT = "/api/enabled";
    static final String ENABLED = "enabled";
    static final String DISABLED = "disabled";

    @Autowired
    MockMvc mvc;

    @MockBean
    ProducerPublisher producerPublisher;

    @Test
    void getMethodShouldReturnEnabledWhenPublisherEnabled() throws Exception {
        when(producerPublisher.isEnabled()).thenReturn(true);
        mvc.perform(get(ENDPOINT).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(ENABLED));
    }

    @Test
    void getMethodShouldReturnDisabledWhenPublisherDisabled() throws Exception {
        when(producerPublisher.isEnabled()).thenReturn(false);
        mvc.perform(get(ENDPOINT).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(DISABLED));
    }

    @Test
    void putMethodShouldEnablePublisher() throws Exception {
        mvc.perform(put(ENDPOINT).contentType(MediaType.APPLICATION_JSON).param(ENABLED, "1"))
                .andExpect(status().isOk());
        verify(producerPublisher).setEnabled(true);
    }

    @Test
    void putMethodShouldDisablePublisher() throws Exception {
        mvc.perform(put(ENDPOINT).contentType(MediaType.APPLICATION_JSON).param(ENABLED, "0"))
                .andExpect(status().isOk());
        verify(producerPublisher).setEnabled(false);
    }
}
