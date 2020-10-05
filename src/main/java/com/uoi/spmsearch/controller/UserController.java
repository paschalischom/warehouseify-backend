package com.uoi.spmsearch.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.uoi.spmsearch.dto.*;
import com.uoi.spmsearch.errorhandling.ResourceNotFoundException;
import com.uoi.spmsearch.service.PointOfInterestService;
import com.uoi.spmsearch.service.SearchService;
import com.uoi.spmsearch.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/user")
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Component
public class UserController {

    private final PointOfInterestService pointOfInterestService;
    private final UserService userService;
    private final SearchService searchService;

    @PostMapping("/create/{uid}")
    public void createUserProfile(@PathVariable("uid") String uid, @RequestBody User user) {
        userService.addUserToFirestore(user, uid);
    }

    @GetMapping("/{userUID}")
    public User getUserProfile(@PathVariable("userUID") String uid)
            throws ResourceNotFoundException, ExecutionException, InterruptedException {
        return userService.getUserFromFirestore(uid);
    }

    @GetMapping("/{userUID}/poi/list")
    public HashMap<String, PointOfInterest> getUserPointsOfInterest(@PathVariable("userUID") String userUID)
            throws ExecutionException, InterruptedException {
        return userService.getUserPointsOfInterest(userUID);
    }

    @PostMapping("/{userUID}/poi/createandadd")
    public Map<String, PointOfInterest> createAndAddPointOfInterest(@PathVariable("userUID") String userUID, @RequestBody LocationRequest locationRequest)
            throws InterruptedException, ExecutionException, IOException {
        PointOfInterest pointOfInterest = pointOfInterestService.createPoIForFirestore(userUID, locationRequest);
        return pointOfInterestService.addPoIToFirestore(userUID, pointOfInterest);
    }

    @PostMapping("/{userUID}/poi/add")
    public Map<String, PointOfInterest> addPointOfInterest(@PathVariable("userUID") String userUID, @RequestBody PointOfInterest pointOfInterest)
            throws InterruptedException, ExecutionException {
        return pointOfInterestService.addPoIToFirestore(userUID, pointOfInterest);
    }

    @GetMapping("/{userUID}/poi/{poiUID}/delete")
    public void deletePointOfInterest(@PathVariable("userUID") String userUID, @PathVariable("poiUID") String poiUID)
            throws ExecutionException, InterruptedException {
        pointOfInterestService.deletePoIFromFirestore(userUID, poiUID);
    }

    @PostMapping("/{userUID}/poi/batch/delete")
    public void deletePointOfInterestBatch(@PathVariable("userUID") String userUID, @RequestBody String[] poiUIDs)
            throws ExecutionException, InterruptedException {
        pointOfInterestService.deletePoiBatchFromFirestore(userUID, poiUIDs);
    }

    @PostMapping("/{userUID}/poi/{poiUID}/edit")
    public void editPointOfInterest(@PathVariable("userUID") String userUID, @PathVariable("poiUID") String poiUID, @RequestBody PointOfInterest newPoI)
            throws ExecutionException, InterruptedException {
        pointOfInterestService.editPoIFromFirestore(userUID, poiUID, newPoI);
    }

    @PostMapping("/{userUID}/warehouseify")
    public List<Listing> searchWarehouseify(@PathVariable("userUID") String userUID, @RequestBody QueryCustomization queryCustomization)
            throws ExecutionException, InterruptedException, IOException {
        return searchService.searchWarehousify(userUID, queryCustomization);
    }
}
