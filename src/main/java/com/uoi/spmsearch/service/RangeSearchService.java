package com.uoi.spmsearch.service;

import com.uoi.spmsearch.dto.*;
import com.uoi.spmsearch.model.Distance;
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

    private List<RTreeBase> fetchNodeChildren(RTreeNode rTreeNode) throws ExecutionException, InterruptedException {
        return firestoreService.readRTreeBasesToObject(rTreeNode);
    }

    private Listing fetchLeafData(RTreeLeaf rTreeLeaf) throws ExecutionException, InterruptedException {
        return firestoreService.readListingToObject(rTreeLeaf.getId());
    }

    private List<RTreeBase> unpackRTreeNodes(List<RTreeBase> nodes) throws ExecutionException, InterruptedException {
        List<RTreeBase> children = new ArrayList<>();
        for (RTreeBase node: nodes) {
            children.addAll(fetchNodeChildren((RTreeNode) node));
        }
        return children;
    }

    private List<Listing> unpackRTreeLeafs(List<RTreeBase> leafs, Map<String, Map<String, Distance>> distanceMatrix) throws ExecutionException, InterruptedException {
        List<Listing> listings = new ArrayList<>();
        for (RTreeBase leaf: leafs) {
            Listing result = fetchLeafData((RTreeLeaf) leaf);
            Map<String, DistanceDTO> resultDistances = new HashMap<>();
            // Assign to poi each distance
            for (Map.Entry<String, Map<String, Distance>> origin : distanceMatrix.entrySet()) {
                DistanceDTO distanceDTO = new DistanceDTO(origin.getValue().get(leaf.getId()).getDistance(), origin.getValue().get(leaf.getId()).getAccuracy());
                resultDistances.put(origin.getKey(), distanceDTO);

            }
            result.setDistancesToPoi(resultDistances);
            listings.add(result);
        }
        return listings;
    }

    private List<RTreeBase> filterOutNodes(PointOfInterest origin, List<RTreeBase> nodes, Map<String, Distance> distanceMatrix) {
        return nodes.stream()
                    .filter(node -> distanceMatrix.get(node.getId()).getDistance() <= origin.getRadius())
                    .collect(Collectors.toList());
    }

    public List<Listing> rangeQuerySearch(Map<String, PointOfInterest> origins)
            throws ExecutionException, InterruptedException, IOException {
        List<Listing> results = new ArrayList<>();
        List<RTreeBase> currentChildren = new ArrayList<>(firestoreService.readRTreeBasesToObject(null));
        while (!currentChildren.isEmpty()) {
            Map<String, Map<String, Distance>> distanceMatrix = new HashMap<>();
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

            if (!currentChildren.isEmpty()) {
                if (currentChildren.get(0) instanceof RTreeNode) {
                    System.out.println("Pre: " + currentChildren.size());
                    currentChildren = unpackRTreeNodes(currentChildren);
                    System.out.println("Post: " + currentChildren.size());
                } else if (currentChildren.get(0) instanceof RTreeLeaf) {
                    results = unpackRTreeLeafs(currentChildren, distanceMatrix);
                    break; // Stop parsing the tree since we reached rock-bottom
                }
            }
        }

        return results;
    }
}
