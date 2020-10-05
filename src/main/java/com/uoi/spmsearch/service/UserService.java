package com.uoi.spmsearch.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.uoi.spmsearch.dto.PointOfInterest;
import com.uoi.spmsearch.dto.User;
import com.uoi.spmsearch.errorhandling.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class UserService {

    private final Firestore db;

    private DocumentSnapshot getUserSnapshot(String userUID) throws ExecutionException, InterruptedException {
        DocumentReference docRef = db.collection("users").document(userUID);
        ApiFuture<DocumentSnapshot> future = docRef.get();

        return future.get();
    }

    public void addUserToFirestore(User user, String userUID) {
        ApiFuture<WriteResult> future = db.collection("users").document(userUID).set(user);
    }

    public User getUserFromFirestore(String userUID) throws ExecutionException, InterruptedException {
        DocumentSnapshot document = getUserSnapshot(userUID);

        if (document.exists()) {
            return document.toObject(User.class);
        } else {
            throw new ResourceNotFoundException(User.class, userUID);
        }
    }

    public void deleteUserFromFirestore(String userUID) {
        ApiFuture<WriteResult> writeResult = db.collection("users").document(userUID).delete();
    }

    public boolean userExists(String userUID) throws ExecutionException, InterruptedException {
        DocumentSnapshot document = getUserSnapshot(userUID);
        return document.exists();
    }

    private HashMap<String, PointOfInterest> getPoIMapFromFuture(ApiFuture<QuerySnapshot> future) throws ExecutionException, InterruptedException {
        HashMap<String, PointOfInterest> pointsOfInterest = new HashMap<>();

        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        if (!documents.isEmpty()) {
            for (DocumentSnapshot snapshot : documents) {
                pointsOfInterest.put(snapshot.getId(), snapshot.toObject(PointOfInterest.class));
            }
        }
        return pointsOfInterest;
    }

    public HashMap<String, PointOfInterest> getUserPointsOfInterest(String userUID) throws ExecutionException, InterruptedException {
        if (userExists(userUID)) {
            ApiFuture<QuerySnapshot> future = db.collection("users").document(userUID).collection("pointsOfInterest").get();
            return getPoIMapFromFuture(future);
        } else {
            return new HashMap<>(); // An error will already have been returned by the userExist check.
        }
    }

    public HashMap<String, PointOfInterest> getUserActivePointsOfInterest(String userUID) throws ExecutionException, InterruptedException {
        if (userExists(userUID)) {
            CollectionReference pointsOfInterestCollection = db.collection("users").document(userUID).collection("pointsOfInterest");
            Query query = pointsOfInterestCollection.whereEqualTo("status", "Active");
            ApiFuture<QuerySnapshot> future = query.get();
            return getPoIMapFromFuture(future);
        } else {
            return new HashMap<>(); // An error will already have been returned by the userExist check.
        }
    }
}
