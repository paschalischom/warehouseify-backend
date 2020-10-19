package com.uoi.spmsearch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uoi.spmsearch.controller.GeocodingController;
import com.uoi.spmsearch.dto.AddressRequest;
import com.uoi.spmsearch.dto.LocationRequest;
import com.uoi.spmsearch.service.GeocodingService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = GeocodingController.class)
public class GeocodingControllerIntegrationTest {

    @MockBean
    private GeocodingService geocodingService;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mvc;

    @Test
    public void verifyHTTPRequestMatching_getForwardGeocode() throws Exception {
        mvc.perform(post("/api/geocoding/forward")
                .contentType("application/json"))
                .andExpect(status().isOk());
    }

    @Test
    public void verifyHTTPRequestMatching_getReverseGeocode() throws Exception {
        mvc.perform(post("/api/geocoding/reverse")
                .contentType("application/json"))
                .andExpect(status().isOk());
    }

    @Test
    public void whenValidInput_thenMapsToBusinessModel_getForwardGeocode() throws Exception {
        AddressRequest addressRequest = new AddressRequest("Amphitheater 16");

        mvc.perform(post("/api/geocoding/forward")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(addressRequest)))
                .andExpect(status().isOk());

        ArgumentCaptor<AddressRequest> addressRequestArgumentCaptor = ArgumentCaptor.forClass(AddressRequest.class);
        verify(geocodingService, times(1)).geocodeAddressToPointOfInterest(addressRequestArgumentCaptor.capture());
        assertThat(addressRequestArgumentCaptor.getValue().getAddress()).isEqualTo("Amphitheater 16");
    }

    @Test
    public void whenValidInput_thenMapsToBusinessModel_getReverseGeocode() throws Exception {
        LocationRequest locationRequest = new LocationRequest(12.21, 21.21);

        mvc.perform(post("/api/geocoding/reverse")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(locationRequest)))
                .andExpect(status().isOk());

        ArgumentCaptor<LocationRequest> locationRequestArgumentCaptor = ArgumentCaptor.forClass(LocationRequest.class);
        verify(geocodingService, times(1)).geocodeLocationToPointOfInterest(locationRequestArgumentCaptor.capture());
        assertThat(locationRequestArgumentCaptor.getValue().getLatitude()).isEqualTo(12.21);
        assertThat(locationRequestArgumentCaptor.getValue().getLongitude()).isEqualTo(21.21);
    }
}
