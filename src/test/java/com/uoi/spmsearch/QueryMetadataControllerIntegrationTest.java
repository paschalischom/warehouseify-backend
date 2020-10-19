package com.uoi.spmsearch;

import com.uoi.spmsearch.controller.QueryMetadataController;
import com.uoi.spmsearch.controller.StateController;
import com.uoi.spmsearch.dto.queryranges.QueryMetadata;
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
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = QueryMetadataController.class)
public class QueryMetadataControllerIntegrationTest {

    @MockBean
    private FirestoreService firestoreService;

    @Autowired
    private MockMvc mvc;

    @Test
    public void verifyHTTPRequestMatching() throws Exception {
        mvc.perform(get("/api/querymetadata")
                .contentType("application/json"))
                .andExpect(status().isOk());
    }

    @Test
    public void whenValidInput_thenMapsToBusinessModel() throws Exception {
        mvc.perform(get("/api/querymetadata")
                .contentType("application/json"))
                .andExpect(status().isOk());
        verify(firestoreService, times(1)).readQueryMetadataToObject();
    }

    @Test
    public void whenValidInput_thenReturnsQueryMetadata() throws Exception {
        List<String> buildingClasses = Arrays.asList("A", "B", "C");
        QueryMetadata queryMetadata = new QueryMetadata(buildingClasses, null, null, null);

        given(firestoreService.readQueryMetadataToObject()).willReturn(queryMetadata);
        mvc.perform(get("/api/querymetadata")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.buildingClasses[0]", is(queryMetadata.getBuildingClasses().get(0))));
    }
}
