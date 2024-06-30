package com.example.eventplanning;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class ManageProfileActivity extends AppCompatActivity {

    private EditText editTextDisplayName;
    private ImageView imageViewAvatar;
    private Button buttonSaveProfile;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    private Uri imageUri;
    private ProgressDialog progressDialog;
    private boolean isImageSelected = false;

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_profile);

        editTextDisplayName = findViewById(R.id.edit_text_display_name);
        imageViewAvatar = findViewById(R.id.image_view_avatar);
        buttonSaveProfile = findViewById(R.id.btn_save_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating profile...");
        progressDialog.setCancelable(false);

        loadUserProfile();

        imageViewAvatar.setOnClickListener(v -> chooseImage());

        buttonSaveProfile.setOnClickListener(v -> saveUserProfile());
    }

    private void loadUserProfile() {
        String userId = mAuth.getCurrentUser().getUid();
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String displayName = documentSnapshot.getString("displayName");
                        editTextDisplayName.setText(displayName);
                        // Load avatar if available
                        // Example: String avatarUrl = documentSnapshot.getString("avatarUrl");
                        // Glide.with(this).load(avatarUrl).into(imageViewAvatar);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ManageProfileActivity.this, "Error loading profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageViewAvatar.setImageURI(imageUri);
            isImageSelected = true;
        }
    }

    private void saveUserProfile() {
        final String displayName = editTextDisplayName.getText().toString().trim();

        if (TextUtils.isEmpty(displayName)) {
            editTextDisplayName.setError("Please enter your display name");
            return;
        }

        progressDialog.show();

        String userId = mAuth.getCurrentUser().getUid();
        Map<String, Object> user = new HashMap<>();
        user.put("displayName", displayName);

        // Check if an image is selected
        if (isImageSelected) {
            uploadAvatar(userId);
        }

        // Update user profile data
        db.collection("users").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Document exists, perform update
                            db.collection("users").document(userId).update(user)
                                    .addOnCompleteListener(updateTask -> {
                                        progressDialog.dismiss();
                                        if (updateTask.isSuccessful()) {
                                            Toast.makeText(ManageProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(ManageProfileActivity.this, "Failed to update profile: " + updateTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            // Document doesn't exist, create new document
                            db.collection("users").document(userId).set(user)
                                    .addOnCompleteListener(setTask -> {
                                        progressDialog.dismiss();
                                        if (setTask.isSuccessful()) {
                                            Toast.makeText(ManageProfileActivity.this, "Profile created successfully", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(ManageProfileActivity.this, "Failed to create profile: " + setTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(ManageProfileActivity.this, "Error checking user profile: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void uploadAvatar(String userId) {
        if (imageUri != null) {
            StorageReference profileImageRef = storageReference.child("avatars/" + userId + ".jpg");
            profileImageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Handle successful upload
                        profileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String avatarUrl = uri.toString();
                            // Update user document with avatarUrl
                            db.collection("users").document(userId).update("avatarUrl", avatarUrl)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(ManageProfileActivity.this, "Avatar uploaded successfully", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(ManageProfileActivity.this, "Failed to upload avatar: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                        // Clear imageUri and reset flag
                                        imageUri = null;
                                        isImageSelected = false;
                                    });
                        });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(ManageProfileActivity.this, "Failed to upload avatar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }
}
