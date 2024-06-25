package com.example.eventplanning;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<Event> eventList;

    public EventAdapter(List<Event> eventList) {
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.textViewEventName.setText(event.getName());
        holder.textViewEventLocation.setText(event.getLocation());
        holder.textViewEventDate.setText(event.getDate());
        holder.textViewEventTime.setText(event.getTime());
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewEventName, textViewEventLocation, textViewEventDate, textViewEventTime;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewEventName = itemView.findViewById(R.id.text_event_name);
            textViewEventLocation = itemView.findViewById(R.id.text_event_location);
            textViewEventDate = itemView.findViewById(R.id.text_view_event_date);
            textViewEventTime = itemView.findViewById(R.id.text_view_event_time);
        }
    }
}