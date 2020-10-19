package com.uoi.spmsearch;

import com.uoi.spmsearch.controller.StateController;
import com.uoi.spmsearch.dto.State;
import com.uoi.spmsearch.service.FirestoreService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = StateController.class)
public class StateControllerIntegrationTest {

    @MockBean
    private FirestoreService firestoreService;

    @Autowired
    private MockMvc mvc;

    @Test
    public void verifyHTTPRequestMatching() throws Exception {
        mvc.perform(get("/api/states")
                .contentType("application/json"))
                .andExpect(status().isOk());
    }

    @Test
    public void whenValidInput_thenMapsToBusinessModel() throws Exception {
        mvc.perform(get("/api/states")
                .contentType("application/json"))
                .andExpect(status().isOk());
        verify(firestoreService, times(1)).readStatesToObjectList();
    }

    @Test
    public void whenValidInput_thenReturnsStateList() throws Exception {
        State state = new State("texas",0, null, null, null, null);
        List<State> states = Arrays.asList(state);

        given(firestoreService.readStatesToObjectList()).willReturn(states);
        mvc.perform(get("/api/states")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is(state.getName())));
    }
}
