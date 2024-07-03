package com.example.eventplanning;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    public interface OnEventClickListener {
        void onEventClick(Event event);
        void onDeleteClick(Event event);
    }

    private final List<Event> events;
    private final OnEventClickListener listener;

    public EventAdapter(List<Event> events, OnEventClickListener listener) {
        this.events = events;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_item, parent, false);
        return new EventViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event currentEvent = events.get(position);
        holder.bind(currentEvent, listener);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {

        private final TextView eventName;
        private final TextView eventLocation;
        private final TextView eventDate;
        private final TextView eventTime;
        private final ImageView eventImage;
        private final Button editButton;
        private final Button deleteButton;

        public EventViewHolder(View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.event_name);
            eventLocation = itemView.findViewById(R.id.event_location);
            eventDate = itemView.findViewById(R.id.event_date);
            eventTime = itemView.findViewById(R.id.event_time);
            eventImage = itemView.findViewById(R.id.event_image);
            editButton = itemView.findViewById(R.id.edit_event_button);
            deleteButton = itemView.findViewById(R.id.delete_event_button);
        }

        public void bind(final Event event, final OnEventClickListener listener) {
            eventName.setText(event.getName());
            eventLocation.setText(event.getLocation());
            eventDate.setText(event.getDate());
            eventTime.setText(event.getTime());
            Glide.with(itemView.getContext())
                    .load(event.getImage())
                    .into(eventImage);

            itemView.setOnClickListener(v -> listener.onEventClick(event));
            editButton.setOnClickListener(v -> listener.onEventClick(event));
            deleteButton.setOnClickListener(v -> listener.onDeleteClick(event));
        }
    }
}
