package com.uoi.spmsearch.controller;

import com.uoi.spmsearch.model.State;
import com.uoi.spmsearch.service.FirestoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api")
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Component
public class StateController {

    private final FirestoreService firestoreService;

    @GetMapping("/states")
    @Cacheable("states")
    public List<State> getAllStates() throws ExecutionException, InterruptedException {
        return firestoreService.readStatesToObjectList();
    }
}
