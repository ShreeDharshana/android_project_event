package com.example.eventplanning;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SendInvitationActivity extends AppCompatActivity {

    private EditText editTextInviteeEmail;
    private Button buttonSendInvitation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_invitation);

        editTextInviteeEmail = findViewById(R.id.edit_text_invitee_email);
        buttonSendInvitation = findViewById(R.id.btn_send_invitation);

        buttonSendInvitation.setOnClickListener(v -> sendInvitation());
    }

    private void sendInvitation() {
        String inviteeEmail = editTextInviteeEmail.getText().toString().trim();

        if (inviteeEmail.isEmpty()) {
            editTextInviteeEmail.setError("Please enter the invitee's email");
            return;
        }

        // Create an email intent
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{inviteeEmail});
        intent.putExtra(Intent.EXTRA_SUBJECT, "You're Invited!");
        intent.putExtra(Intent.EXTRA_TEXT, "You have been invited to an event. Please check the event details in the app.");

        try {
            startActivity(Intent.createChooser(intent, "Send Email"));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(SendInvitationActivity.this, "No email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }
}
