package com.uoi.spmsearch.controller;

import com.uoi.spmsearch.dto.queryranges.QueryRanges;
import com.uoi.spmsearch.service.FirestoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api")
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Component
public class QueryRangesController {

    private final FirestoreService firestoreService;

    @GetMapping("/queryranges")
    public QueryRanges getQueryRanges() throws ExecutionException, InterruptedException {
        return firestoreService.readQueryRangesToObject();
    }
}
