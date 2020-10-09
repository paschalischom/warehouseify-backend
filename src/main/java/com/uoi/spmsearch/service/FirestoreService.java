package com.uoi.spmsearch.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.uoi.spmsearch.dto.*;
import com.uoi.spmsearch.dto.queryranges.QueryMetadata;
import com.uoi.spmsearch.errorhandling.ResourceNotFoundException;
import com.uoi.spmsearch.dto.Listing;
import com.uoi.spmsearch.model.State;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class FirestoreService {

    private final Firestore db;

    public HashMap<String, Listing> readListingsToObjectList(String state) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = db.collection("states").document(state).collection("listings").get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        HashMap<String, Listing> listings = new HashMap<>();
        if (!documents.isEmpty()) {
            for (DocumentSnapshot snapshot: documents) {
                listings.put(snapshot.getId(), snapshot.toObject(Listing.class));
            }
        }
        return listings;
    }

    public List<State> readStatesToObjectList() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = db.collection("states").get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        List<State> states = new ArrayList<>();
        if (!documents.isEmpty()) {
            for (DocumentSnapshot snapshot: documents) {
                states.add(snapshot.toObject(State.class));
            }
        }
        return states;
    }

    public QueryMetadata readQueryMetadataToObject() throws ExecutionException, InterruptedException {
        DocumentReference docRef = db.collection("ui").document("queryMetadata");
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
        if (document.exists()) {
            return document.toObject(QueryMetadata.class);
        } else {
            throw new ResourceNotFoundException(QueryMetadata.class, "queryMetadata");
        }
    }

    @Cacheable(value = "rTree", key = "#parentNode.id", condition = "#parentNode != null")
    public List<RTreeBase> readRTreeBasesToObject(RTreeNode parentNode) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future;
        if (parentNode == null) {
            future = db.collection("rTree").get();
        } else {
            future = parentNode.getChildrenRef().get();
        }
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        List<RTreeBase> childBases = new ArrayList<>();
        if (!documents.isEmpty()) {
            for (DocumentSnapshot snapshot: documents) {
                if (snapshot.contains("mbr")) {
                    RTreeNode childNode = snapshot.toObject(RTreeNode.class);
                    assert childNode != null;
                    CollectionReference childrenRef = snapshot.getReference().collection("children");
                    childNode.setChildrenRef(childrenRef);
                    childBases.add(childNode);
                } else {
                    RTreeLeaf childLeaf = snapshot.toObject(RTreeLeaf.class);
                    assert childLeaf != null;
                    childBases.add(childLeaf);
                }
            }
        }
        return childBases;
    }

    public Set<Listing> readListingsToObjectList(List<String> listingUIDs) throws ExecutionException, InterruptedException {
        CollectionReference listingsRef = db.collection("listings");

        Set<Listing> listings = new HashSet<>();
        // Does not work due to https://github.com/googleapis/nodejs-firestore/issues/990
        // Plus, I think requesting specific documents by id is faster than querying
        /*
        for (int i=0; i < listingUIDs.size(); i += 10) {
            Query query = listingsRef.whereIn(FieldPath.documentId(), listingUIDs.subList(i, Math.min(i+10, listingUIDs.size())));
            ApiFuture<QuerySnapshot> future = query.get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (DocumentSnapshot document: documents) {
                listings.add(document.toObject(Listing.class));
            }
        }
        */

        return listings;
    }

    public Listing readListingToObject(String listingUID) throws ExecutionException, InterruptedException {
        DocumentReference docRef = db.collection("listings").document(listingUID);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
        if (document.exists()) {
            return document.toObject(Listing.class);
        } else {
            throw new ResourceNotFoundException(Listing.class, "listing");
        }
    }
}
