package com.example.notes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity implements RecyclerViewInterface{

    Button create;
    RecyclerView rv;
    CheckBox gridBox;
    ArrayList<Note> notes;
    MyDB myDB;
    NoteAdapter adapter;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    EditText search;
    LinearLayout parentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        create = findViewById(R.id.create);
        rv = findViewById(R.id.rv);
        gridBox = findViewById(R.id.gridBox);
        search = findViewById(R.id.search);
        parentLayout = findViewById(R.id.main);

        sharedPreferences = getApplicationContext().getSharedPreferences("layoutPreference", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        // make arraylist
        notes = new ArrayList<>();

        // link arraylist with adapter
        adapter = new NoteAdapter(notes, this, getApplicationContext(), getResources().getColor(R.color.orange));

        if (!sharedPreferences.contains("isGrid")) {
            editor.putBoolean("isGrid", false);
            editor.apply();
        }

        // set layout based on the preference
        boolean isGrid = sharedPreferences.getBoolean("isGrid", false);
        if (isGrid) {
            rv.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
            gridBox.setChecked(true);
        } else {
            rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        }
        rv.setAdapter(adapter);

        gridBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // to change layout
                adapter.toggleLayout();
                if (isChecked) {
                    editor.putBoolean("isGrid", true);
                    rv.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
                } else {
                    editor.putBoolean("isGrid", false);
                    rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                }
                editor.apply();
            }
        });

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(MainActivity.this, MakeNote.class);
                startActivity(in);
            }
        });

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.setSearchText(s.toString());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

//        parentLayout.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                    if (search.isFocused()) {
//                        Rect rect = new Rect();
//
//                        // gets the coordinates of search editText in global rectangle view and stores in rect
//                        search.getGlobalVisibleRect(rect);
//                        if (!rect.contains((int) event.getRawX(), (int) event.getRawY())) {
//                            search.clearFocus();
//                            InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//                            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
//                        }
//
//                    }
//                }
//                return false;
//            }
//        });

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect rect = new Rect();
                search.getGlobalVisibleRect(rect);
                if (!rect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    search.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    public void getAllNotes() {
        // getting notes from db
        Cursor c = myDB.SelectAll();
        if (c.getCount() > 0) {
            c.moveToFirst();
            for (int i = 0; i < c.getCount(); i++) {
                int id = c.getInt(0);
                String title = c.getString(1);
                String content = c.getString(2);

                Note note = new Note(id, title, content);
                notes.add(note);
                c.moveToNext();
            }
            c.close();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        myDB = new MyDB(getApplicationContext());
        notes.clear();
        getAllNotes();
        myDB.close();
        Collections.reverse(notes);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onNoteClick(int position) {
        Intent in = new Intent(getApplicationContext(), MakeNote.class);
        in.putExtra("id", notes.get(position).getId());
        in.putExtra("title", notes.get(position).getTitle());
        in.putExtra("content", notes.get(position).getContent());
        startActivity(in);
    }
}