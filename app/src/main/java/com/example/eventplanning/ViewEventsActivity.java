package com.example.eventplanning;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ViewEventsActivity extends AppCompatActivity {

    RecyclerView recyclerViewEvents;
    EventAdapter eventAdapter;
    List<Event> eventList;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_events);

        recyclerViewEvents = findViewById(R.id.recycler_view_events);
        recyclerViewEvents.setLayoutManager(new LinearLayoutManager(this));
        eventList = new ArrayList<>();
        eventAdapter = new EventAdapter(eventList);
        recyclerViewEvents.setAdapter(eventAdapter);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loadEvents();
    }

    private void loadEvents() {
        String userId = mAuth.getCurrentUser().getUid();
        db.collection("events")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        eventList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Event event = document.toObject(Event.class);
                            event.setId(document.getId());
                            eventList.add(event);
                        }
                        eventAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(ViewEventsActivity.this, "Error getting events.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

        private List<Event> events;

        EventAdapter(List<Event> events) {
            this.events = events;
        }

        @NonNull
        @Override
        public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.event_item, parent, false);
            return new EventViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
            Event event = events.get(position);
            holder.textEventName.setText(event.getName());
            holder.textEventLocation.setText(event.getLocation());
            holder.textEventDatetime.setText(event.getDate() + " " + event.getTime());

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(ViewEventsActivity.this, EventDetailsActivity.class);
                intent.putExtra("eventName", event.getName());
                intent.putExtra("eventLocation", event.getLocation());
                intent.putExtra("eventDateTime", event.getDate() + " " + event.getTime());
                startActivity(intent);
            });

            holder.buttonDeleteEvent.setOnClickListener(v -> {
                db.collection("events").document(event.getId())
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            events.remove(position);
                            notifyItemRemoved(position);
                            Toast.makeText(ViewEventsActivity.this, "Event deleted", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(ViewEventsActivity.this, "Error deleting event", Toast.LENGTH_SHORT).show();
                        });
            });
        }

        @Override
        public int getItemCount() {
            return events.size();
        }

        class EventViewHolder extends RecyclerView.ViewHolder {

            TextView textEventName, textEventLocation, textEventDatetime;
            Button buttonEditEvent, buttonDeleteEvent;

            EventViewHolder(@NonNull View itemView) {
                super(itemView);
                textEventName = itemView.findViewById(R.id.text_event_name);
                textEventLocation = itemView.findViewById(R.id.text_event_location);
                textEventDatetime = itemView.findViewById(R.id.text_event_datetime);
                buttonEditEvent = itemView.findViewById(R.id.button_edit_event);
                buttonDeleteEvent = itemView.findViewById(R.id.button_delete_event);
            }
        }
    }
}
