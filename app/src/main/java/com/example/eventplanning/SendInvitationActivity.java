package com.example.eventplanning;

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

        // Initialize views
        editTextInviteeEmail = findViewById(R.id.edit_text_invitee_email);
        buttonSendInvitation = findViewById(R.id.btn_send_invitation);

        // Send invitation button click listener
        buttonSendInvitation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendInvitation();
            }
        });
    }

    private void sendInvitation() {
        String inviteeEmail = editTextInviteeEmail.getText().toString().trim();

        // Validate email format
        if (!isValidEmail(inviteeEmail)) {
            editTextInviteeEmail.setError("Invalid email");
            return;
        }

        // Send invitation logic
        // Example: Use email API to send invitation

        // Notify user
        Toast.makeText(this, "Invitation sent to " + inviteeEmail, Toast.LENGTH_SHORT).show();

        // Clear input
        editTextInviteeEmail.setText("");
    }

    private boolean isValidEmail(CharSequence target) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}
