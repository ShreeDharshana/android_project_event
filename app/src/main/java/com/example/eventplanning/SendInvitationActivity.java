package com.example.eventplanning;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class SendInvitationActivity extends AppCompatActivity {

    private Button btnSendInvitation;
    private EditText editTextInviteeEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_invitation);

        btnSendInvitation = findViewById(R.id.btn_send_invitation);
        editTextInviteeEmail = findViewById(R.id.edit_text_invitee_email);

        btnSendInvitation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMostRecentEventDetails();
            }
        });
    }

    private void getMostRecentEventDetails() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                            String name = documentSnapshot.getString("name");
                            String location = documentSnapshot.getString("location");
                            String date = documentSnapshot.getString("date");
                            String time = documentSnapshot.getString("time");
                            String imageUrl = documentSnapshot.getString("imageUrl");

                            composeEmail(name, location, date, time, imageUrl);
                        } else {
                            Toast.makeText(SendInvitationActivity.this, "No recent event found", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SendInvitationActivity.this, "Failed to get event details", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void composeEmail(String name, String location, String date, String time, String imageUrl) {
        String recipientEmail = editTextInviteeEmail.getText().toString().trim();
        if (recipientEmail.isEmpty()) {
            Toast.makeText(SendInvitationActivity.this, "Please enter a recipient email address", Toast.LENGTH_SHORT).show();
            return;
        }

        String subject = "Invitation to " + name+"!!";
        String message = "Hey! You have been invited to an event. Please check the event details below.\n\n"
                + "Event Name: " + name + "\n"
                + "Location: " + location + "\n"
                + "Date: " + date + "\n"
                + "Time: " + time + "\n\n"
                +"Please contact the event organizer for further details or acknowledge whether you are attending the event or not by responding to this mail.\n\n"
                + "Thank you!";

        if (imageUrl != null && !imageUrl.isEmpty()) {
            message += "Event Image: " + imageUrl + "\n";
        }

        sendEmail(recipientEmail, subject, message);
    }

    private void sendEmail(String recipientEmail, String subject, String message) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{recipientEmail});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);

        try {
            startActivity(Intent.createChooser(emailIntent, "Send email using..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(SendInvitationActivity.this, "No email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }
}
