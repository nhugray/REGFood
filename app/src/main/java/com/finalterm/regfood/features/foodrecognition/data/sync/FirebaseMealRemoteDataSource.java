package com.finalterm.regfood.features.foodrecognition.data.sync;

import android.net.Uri;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FirebaseMealRemoteDataSource implements MealRemoteDataSource {

    private final FirebaseFirestore firestore;
    private final FirebaseStorage storage;

    public FirebaseMealRemoteDataSource() {
        this(FirebaseFirestore.getInstance(), FirebaseStorage.getInstance());
    }

    public FirebaseMealRemoteDataSource(FirebaseFirestore firestore, FirebaseStorage storage) {
        this.firestore = firestore;
        this.storage = storage;
    }

    @Override
    public void uploadMealImage(File imageFile, String mealId, UploadCallback callback) {
        if (imageFile == null || !imageFile.exists()) {
            callback.onError(new IllegalArgumentException("imageFile is missing"));
            return;
        }

        String fileName = imageFile.getName();
        String safeMealId = mealId != null && !mealId.isEmpty() ? mealId : UUID.randomUUID().toString();
        StorageReference ref = storage.getReference()
                .child("meal_images")
                .child(safeMealId)
                .child(fileName);

        ref.putFile(Uri.fromFile(imageFile))
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException() != null ? task.getException() : new IllegalStateException("Upload failed");
                    }
                    return ref.getDownloadUrl();
                })
                .addOnSuccessListener(uri -> callback.onSuccess(uri.toString()))
                .addOnFailureListener(callback::onError);
    }

    @Override
    public void syncMealLog(MealSyncPayload payload, SyncCallback callback) {
        if (payload == null) {
            callback.onError(new IllegalArgumentException("payload is null"));
            return;
        }
        if (payload.userUid == null || payload.userUid.isEmpty()) {
            callback.onError(new IllegalArgumentException("userUid is required"));
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("mealId", payload.mealId);
        data.put("userUid", payload.userUid);
        data.put("foodName", payload.foodName);
        data.put("aiLabel", payload.aiLabel);
        data.put("confidence", payload.confidence);
        data.put("portionName", payload.portionName);
        data.put("portionMultiplier", payload.portionMultiplier);
        data.put("toppings", payload.toppings);
        data.put("totalCalories", payload.totalCalories);
        data.put("mealType", payload.mealType);
        data.put("eatenAt", payload.eatenAt);
        data.put("imageLocalPath", payload.imageLocalPath);
        data.put("imageDownloadUrl", payload.imageDownloadUrl);

        String documentPath = "users/" + payload.userUid + "/meal_logs/" + payload.mealId;
        firestore.document(documentPath)
                .set(data)
                .addOnSuccessListener(unused -> callback.onSuccess(documentPath, payload.imageDownloadUrl))
                .addOnFailureListener(callback::onError);
    }
}
