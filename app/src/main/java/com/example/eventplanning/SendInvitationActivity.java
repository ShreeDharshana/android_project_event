package com.example.eventplanning;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SendInvitationActivity extends AppCompatActivity {

    private EditText editTextInviteeEmail;
    private Button buttonSendInvitation;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_invitation);

        editTextInviteeEmail = findViewById(R.id.edit_text_invitee_email);
        buttonSendInvitation = findViewById(R.id.btn_send_invitation);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        buttonSendInvitation.setOnClickListener(v -> sendInvitation());
    }

    private void sendInvitation() {
        String inviteeEmail = editTextInviteeEmail.getText().toString().trim();

        if (TextUtils.isEmpty(inviteeEmail)) {
            Toast.makeText(this, "Please enter the invitee's email", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the current user's ID
        String userId = mAuth.getCurrentUser().getUid();

        // Create invitation data
        Map<String, Object> invitation = new HashMap<>();
        invitation.put("inviteeEmail", inviteeEmail);
        invitation.put("userId", userId);

        // Save invitation to Firestore
        db.collection("invitations")
                .add(invitation)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(SendInvitationActivity.this, "Invitation sent successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SendInvitationActivity.this, MainActivity.class);
                    finish(); // Close the activity after sending the invitation
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(SendInvitationActivity.this, "Error sending invitation: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
