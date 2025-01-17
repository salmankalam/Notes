package com.example.notes;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    // to hold the click listener
    RecyclerViewInterface recyclerViewInterface;
    ArrayList<Note> notes;
    boolean isGrid;
    Context context;
    String searchText = "";
    int color;

    public NoteAdapter(ArrayList<Note> notes, RecyclerViewInterface recyclerViewInterface, Context context, int color) {
        this.notes = notes;
        this.recyclerViewInterface = recyclerViewInterface;
        this.context = context;
        SharedPreferences pref = context.getSharedPreferences("layoutPreference", Context.MODE_PRIVATE);
        isGrid = pref.getBoolean("isGrid", false);
        this.color = color;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText.toLowerCase();
    }
    @Override
    public int getItemViewType(int position) {
        return isGrid ? 1 : 0;
        // 0 for list and 1 for grid
    }

    @NonNull
    @Override
    public NoteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutType = (viewType == 0) ? R.layout.note_layout : R.layout.grid_note_layout;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(layoutType, parent, false);
        return new ViewHolder(v, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteAdapter.ViewHolder holder, int position) {
        // get each note from arraylist and set it to the layout
        // if a string is searched highlight and set spannable string to the layout
        Note note = notes.get(position);

        String title = note.getTitle();
        String content = note.getContent();

        SpannableString titleSpan = new SpannableString(title);
        SpannableString contentSpan = new SpannableString(content);

        if (!searchText.isEmpty()) {

            HighlightText(titleSpan, searchText);
            HighlightText(contentSpan, searchText);
        }
        holder.title_layout.setText(titleSpan);
        holder.content_layout.setText(contentSpan);
    }

    public void HighlightText(SpannableString text, String searchText) {
        // indexOf works on strings so store spannable string as strings
        String lowerText = text.toString().toLowerCase();
        String lowerSearchText = searchText.toLowerCase();

        // +ve if found, -ve if not found
        int index = lowerText.indexOf(lowerSearchText);

        while (index >= 0) {
            int end = index + searchText.length();
            text.setSpan(new BackgroundColorSpan(color), index, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            // index of next search text
            index = lowerText.indexOf(lowerSearchText, end);
        }
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public void toggleLayout() {
        isGrid = !isGrid;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title_layout, content_layout;

        public ViewHolder(View v, RecyclerViewInterface recyclerViewInterface) {
            super(v);
            title_layout = v.findViewById(R.id.title_layout);
            content_layout = v.findViewById(R.id.content_layout);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (recyclerViewInterface != null) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            recyclerViewInterface.onNoteClick(pos);
                        }
                    }
                }
            });
        }
    }
}