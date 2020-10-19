package com.uoi.spmsearch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uoi.spmsearch.controller.UserController;
import com.uoi.spmsearch.dto.*;
import com.uoi.spmsearch.service.PointOfInterestService;
import com.uoi.spmsearch.service.SearchService;
import com.uoi.spmsearch.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = UserController.class)
public class UserControllerIntegrationTest {

    @MockBean
    private PointOfInterestService pointOfInterestService;
    @MockBean
    private UserService userService;
    @MockBean
    private SearchService searchService;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mvc;

    @Test
    public void verifyHTTPRequestMatching_createUserProfile() throws Exception {
        User user = new User("test@gmail.com", "test", "case", "testCase", "TC");

        mvc.perform(post("/api/user/create/12")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());
    }

    @Test
    public void verifyHTTPRequestMatching_getUserProfile() throws Exception {
        mvc.perform(get("/api/user/12")
                .contentType("application/json"))
                .andExpect(status().isOk());
    }

    @Test
    public void verifyHTTPRequestMatching_getUserPointsOfInterest() throws Exception {
        mvc.perform(get("/api/user/12/poi/list")
                .contentType("application/json"))
                .andExpect(status().isOk());
    }

    @Test
    public void verifyHTTPRequestMatching_createAndAddPointOfInterest() throws Exception {
        LocationRequest locationRequest = new LocationRequest(12.21, 21.21);

        mvc.perform(post("/api/user/12/poi/createandadd")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(locationRequest)))
                .andExpect(status().isOk());
    }

    @Test
    public void verifyHTTPRequestMatching_addPointOfInterest() throws Exception {
        PointOfInterest pointOfInterest = new PointOfInterest(12.21, 21.21, null, 0.0, null, null, null, true, null, null, null);

        mvc.perform(post("/api/user/12/poi/add")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(pointOfInterest)))
                .andExpect(status().isOk());
    }

    @Test
    public void verifyHTTPRequestMatching_deletePointOfInterest() throws Exception {
        mvc.perform(get("/api/user/12/poi/2/delete")
                .contentType("application/json"))
                .andExpect(status().isOk());
    }

    @Test
    public void verifyHTTPRequestMatching_deletePointOfInterestBatch() throws Exception {
        List<String> poiUIDs = Arrays.asList("1", "2", "3");

        mvc.perform(post("/api/user/12/poi/batch/delete")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(poiUIDs)))
                .andExpect(status().isOk());
    }

    @Test
    public void verifyHTTPRequestMatching_editPointOfInterest() throws Exception {
        PointOfInterest pointOfInterest = new PointOfInterest(12.21, 21.21, null, 0.0, null, null, null, true, null, null, null);

        mvc.perform(post("/api/user/12/poi/1/edit")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(pointOfInterest)))
                .andExpect(status().isOk());
    }

    @Test
    public void verifyHTTPRequestMatching_searchWarehouseify() throws Exception {
        UserQuery userQuery = new UserQuery();

        mvc.perform(post("/api/user/12/warehouseify")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(userQuery)))
                .andExpect(status().isOk());
    }

    @Test
    public void whenValidInput_thenMapsToBusinessModel_createUserProfile() throws Exception {
        User user = new User("test@gmail.com", "test", "case", "testCase", "TC");

        mvc.perform(post("/api/user/create/12")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userService, times(1)).addUserToFirestore(userArgumentCaptor.capture(), eq("12"));
        assertThat(userArgumentCaptor.getValue().getEmail()).isEqualTo("test@gmail.com");
        assertThat(userArgumentCaptor.getValue().getFirstName()).isEqualTo("test");
        assertThat(userArgumentCaptor.getValue().getLastName()).isEqualTo("case");
        assertThat(userArgumentCaptor.getValue().getFullName()).isEqualTo("testCase");
        assertThat(userArgumentCaptor.getValue().getInitials()).isEqualTo("TC");
    }

    @Test
    public void whenValidInput_thenMapsToBusinessModel_getUserProfile() throws Exception {
        mvc.perform(get("/api/user/12")
                .contentType("application/json"))
                .andExpect(status().isOk());

        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(userService, times(1)).getUserFromFirestore(stringArgumentCaptor.capture());
        assertThat(stringArgumentCaptor.getValue()).isEqualTo("12");
    }

    @Test
    public void whenValidInput_thenMapsToBusinessModel_getUserPointsOfInterest() throws Exception {
        mvc.perform(get("/api/user/12/poi/list")
                .contentType("application/json"))
                .andExpect(status().isOk());

        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(userService, times(1)).getUserFromFirestore(stringArgumentCaptor.capture());
        assertThat(stringArgumentCaptor.getValue()).isEqualTo("12");
    }

    @Test
    public void whenValidInput_thenMapsToBusinessModel_createAndAddPointOfInterest() throws Exception {
        LocationRequest locationRequest = new LocationRequest(12.21, 21.21);

        mvc.perform(post("/api/user/12/poi/createandadd")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(locationRequest)))
                .andExpect(status().isOk());

        ArgumentCaptor<LocationRequest> locationRequestArgumentCaptor = ArgumentCaptor.forClass(LocationRequest.class);
        verify(pointOfInterestService, times(1)).createPoIForFirestore(eq("12"), locationRequestArgumentCaptor.capture());
        assertThat(locationRequestArgumentCaptor.getValue().getLatitude()).isEqualTo(12.21);
        assertThat(locationRequestArgumentCaptor.getValue().getLongitude()).isEqualTo(21.21);
    }

    @Test
    public void whenValidInput_thenMapsToBusinessModel_addPointOfInterest() throws Exception {
        PointOfInterest pointOfInterest = new PointOfInterest(12.21, 21.21, "Amphitheater 16", 5000, "Texas", "21-11-20", "Active", false, "ROOFTOP", "OK", null);

        mvc.perform(post("/api/user/12/poi/add")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(pointOfInterest)))
                .andExpect(status().isOk());

        ArgumentCaptor<PointOfInterest> pointOfInterestArgumentCaptor = ArgumentCaptor.forClass(PointOfInterest.class);
        verify(pointOfInterestService, times(1)).addPoIToFirestore(eq("12"), pointOfInterestArgumentCaptor.capture());
        assertThat(pointOfInterestArgumentCaptor.getValue().getLat()).isEqualTo(12.21);
        assertThat(pointOfInterestArgumentCaptor.getValue().getLng()).isEqualTo(21.21);
        assertThat(pointOfInterestArgumentCaptor.getValue().getAddress()).isEqualTo("Amphitheater 16");
        assertThat(pointOfInterestArgumentCaptor.getValue().getRadius()).isEqualTo(5000);
        assertThat(pointOfInterestArgumentCaptor.getValue().getState()).isEqualTo("Texas");
        assertThat(pointOfInterestArgumentCaptor.getValue().getUpdated()).isEqualTo("21-11-20");
        assertThat(pointOfInterestArgumentCaptor.getValue().getStatus()).isEqualTo("Active");
        assertThat(pointOfInterestArgumentCaptor.getValue().getError()).isEqualTo(false);
        assertThat(pointOfInterestArgumentCaptor.getValue().getLocationType()).isEqualTo("ROOFTOP");
        assertThat(pointOfInterestArgumentCaptor.getValue().getResponseStatus()).isEqualTo("OK");
        assertThat(pointOfInterestArgumentCaptor.getValue().getViewport()).isEqualTo(null);
    }

    @Test
    public void whenValidInput_thenMapsToBusinessModel_deletePointOfInterest() throws Exception {
        mvc.perform(get("/api/user/12/poi/1/delete")
                .contentType("application/json"))
                .andExpect(status().isOk());

        ArgumentCaptor<String> userUIDArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> poiUIDArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(pointOfInterestService, times(1)).deletePoIFromFirestore(userUIDArgumentCaptor.capture(), poiUIDArgumentCaptor.capture());
        assertThat(userUIDArgumentCaptor.getValue()).isEqualTo("12");
        assertThat(poiUIDArgumentCaptor.getValue()).isEqualTo("1");
    }

    @Test
    public void whenValidInput_thenMapsToBusinessModel_deletePointOfInterestBatch() throws Exception {
        List<String> poiUIDs = Arrays.asList("1", "2", "3");

        mvc.perform(post("/api/user/12/poi/batch/delete")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(poiUIDs)))
                .andExpect(status().isOk());

        ArgumentCaptor<List<String>> poiListArgumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(pointOfInterestService, times(1)).deletePoiBatchFromFirestore(eq("12"), poiListArgumentCaptor.capture());
        assertThat(poiListArgumentCaptor.getValue().get(0)).isEqualTo("1");
        assertThat(poiListArgumentCaptor.getValue().get(1)).isEqualTo("2");
        assertThat(poiListArgumentCaptor.getValue().get(2)).isEqualTo("3");
    }

    @Test
    public void whenValidInput_thenMapsToBusinessModel_editPointOfInterest() throws Exception {
        PointOfInterest pointOfInterest = new PointOfInterest(12.21, 21.21, "Amphitheater 16", 5000, "Texas", "21-11-20", "Active", false, "ROOFTOP", "OK", null);

        mvc.perform(post("/api/user/12/poi/1/edit")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(pointOfInterest)))
                .andExpect(status().isOk());

        ArgumentCaptor<PointOfInterest> pointOfInterestArgumentCaptor = ArgumentCaptor.forClass(PointOfInterest.class);
        verify(pointOfInterestService, times(1)).editPoIFromFirestore(eq("12"), eq("1"), pointOfInterestArgumentCaptor.capture());
        assertThat(pointOfInterestArgumentCaptor.getValue().getLat()).isEqualTo(12.21);
        assertThat(pointOfInterestArgumentCaptor.getValue().getLng()).isEqualTo(21.21);
        assertThat(pointOfInterestArgumentCaptor.getValue().getAddress()).isEqualTo("Amphitheater 16");
        assertThat(pointOfInterestArgumentCaptor.getValue().getRadius()).isEqualTo(5000);
        assertThat(pointOfInterestArgumentCaptor.getValue().getState()).isEqualTo("Texas");
        assertThat(pointOfInterestArgumentCaptor.getValue().getUpdated()).isEqualTo("21-11-20");
        assertThat(pointOfInterestArgumentCaptor.getValue().getStatus()).isEqualTo("Active");
        assertThat(pointOfInterestArgumentCaptor.getValue().getError()).isEqualTo(false);
        assertThat(pointOfInterestArgumentCaptor.getValue().getLocationType()).isEqualTo("ROOFTOP");
        assertThat(pointOfInterestArgumentCaptor.getValue().getResponseStatus()).isEqualTo("OK");
        assertThat(pointOfInterestArgumentCaptor.getValue().getViewport()).isEqualTo(null);
    }
}
