package com.example.eventplanning;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private Context context;
    private List<Event> eventList;
    private OnEventClickListener listener;

    public EventAdapter(Context context, List<Event> eventList, OnEventClickListener listener) {
        this.context = context;
        this.eventList = eventList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.event_item, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.eventName.setText(event.getName());
        holder.eventLocation.setText(event.getLocation());
        holder.eventDate.setText(event.getDate());
        holder.eventTime.setText(event.getTime());

        holder.deleteButton.setOnClickListener(v -> listener.onDeleteClick(event));
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public interface OnEventClickListener {
        void onDeleteClick(Event event);
    }

    class EventViewHolder extends RecyclerView.ViewHolder {
        TextView eventName, eventLocation, eventDate, eventTime;
        Button deleteButton;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.text_event_name);
            eventLocation = itemView.findViewById(R.id.text_event_location);
            eventDate = itemView.findViewById(R.id.text_event_date);
            eventTime = itemView.findViewById(R.id.text_event_time);
            deleteButton = itemView.findViewById(R.id.button_delete_event);
        }
    }
}
