package com.uoi.spmsearch.service;

import com.uoi.spmsearch.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class RangeSearchService {

    private final FirestoreService firestoreService;
    private final DistanceService distanceService;

    @Autowired
    public RangeSearchService(FirestoreService firestoreService, DistanceService distanceService) {
        this.firestoreService = firestoreService;
        this.distanceService = distanceService;
    }

    private List<RTreeBase> unpackRTreeNode(RTreeNode rTreeNode) throws ExecutionException, InterruptedException {
        return firestoreService.readRTreeBasesToObject(rTreeNode);
    }

    private List<RTreeBase> filterOutNodes(PointOfInterest origin, List<RTreeBase> nodes, Map<String, Integer> distanceMatrix) {
        return nodes.stream()
                    .filter(node -> distanceMatrix.get(node.getId()) <= origin.getRadius())
                    .collect(Collectors.toList());
    }

    public List<String> rangeQuerySearch(Map<String, PointOfInterest> origins) throws ExecutionException, InterruptedException, IOException {
        List<String> results = new ArrayList<>();
        List<RTreeBase> currentChildren = new ArrayList<>(firestoreService.readRTreeBasesToObject(null));
        while (!currentChildren.isEmpty()) {
            Map<String, Map<String, Integer>> distanceMatrix = new HashMap<>();
            for (String originID: origins.keySet()) {
                // While a 'hacky' solution, in our case it is fail-safe due to the structure of the R-Tree.
                // Either all currentChildren will be nodes or all will be leafs.
                if (currentChildren.get(0) instanceof RTreeNode) {
                    distanceMatrix.put(originID, distanceService.calculateHaversineDistance(origins.get(originID), currentChildren));
                } else if (currentChildren.get(0) instanceof RTreeLeaf) {
                    distanceMatrix.put(originID, distanceService.calculateGoogleDistance(origins.get(originID), currentChildren));
                }

                currentChildren = filterOutNodes(origins.get(originID), currentChildren, distanceMatrix.get(originID));
            }

            List<RTreeBase> newChildren = new ArrayList<>();
            for (RTreeBase child: currentChildren) {
                if (child instanceof RTreeNode) {
                    newChildren.addAll(unpackRTreeNode((RTreeNode) child));
                } else if (child instanceof RTreeLeaf) {
                    results.add(child.getId());
                }
            }
            currentChildren = newChildren;
        }

        return results;
    }
}
