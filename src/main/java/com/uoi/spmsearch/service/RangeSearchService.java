package com.uoi.spmsearch.service;

import com.uoi.spmsearch.dto.*;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class RangeSearchService {

    private final FirestoreService firestoreService;
    private final DistanceService distanceService;

    private List<RTreeBase> unpackRTreeNode(RTreeNode rTreeNode) throws ExecutionException, InterruptedException {
        return firestoreService.readRTreeBasesToObject(rTreeNode);
    }

    private List<RTreeBase> filterOutNodes(PointOfInterest origin, List<RTreeBase> nodes, Map<String, Integer> distanceMatrix) {
        return nodes.stream()
                    .filter(node -> distanceMatrix.get(node.getId()) <= origin.getRadius())
                    .collect(Collectors.toList());
    }

    public List<Listing> rangeQuerySearch(Map<String, PointOfInterest> origins)
            throws ExecutionException, InterruptedException, IOException {
        List<Listing> results = new ArrayList<>();
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
                    Listing result = firestoreService.readListingToObject(child.getId());
                    Map<String, Integer> resultDistances = new HashMap<>();
                    for (Map.Entry<String, Map<String, Integer>> origin : distanceMatrix.entrySet()) {
                        resultDistances.put(origin.getKey(), origin.getValue().get(child.getId()));
                    }
                    result.setDistancesToPoi(resultDistances);
                    results.add(result);
                }
            }
            currentChildren = newChildren;
        }

        return results;
    }
}
