package com.example.notes;

import static com.google.android.material.internal.ViewUtils.hideKeyboard;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MakeNote extends AppCompatActivity {

    EditText title, content, search;
    Button ok, delete, back;

    String stitle, scontent;

    MyDB myDB;
    int id = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_note);

        title = findViewById(R.id.title);
        content = findViewById(R.id.content);
        ok = findViewById(R.id.ok);
        delete = findViewById(R.id.delete);
        back = findViewById(R.id.back);
        search = findViewById(R.id.search);

        myDB = new MyDB(getApplicationContext());

        // when clicked on any note
        Bundle b = getIntent().getExtras();
        if (b != null) {
            id = b.getInt("id");
            title.setText(b.getString("title"));
            content.setText(b.getString("content"));
        }

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchText = s.toString().toLowerCase();
                String titleText = title.getText().toString();
                String contentText = content.getText().toString();

                SpannableString titleSpan = new SpannableString(titleText);
                SpannableString contentSpan = new SpannableString(contentText);

                if (!searchText.isEmpty()) {
                    HighlightText(titleSpan, searchText);
                    HighlightText(contentSpan, searchText);
                }
                title.setText(titleSpan);
                content.setText(contentSpan);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // update note
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stitle = title.getText().toString();
                scontent = content.getText().toString();

                // update note
                if (id != -1) {
                    int result = myDB.Update(id, stitle, scontent);
                    if (result > 0) {
                        Toast.makeText(getApplicationContext(), "Updated", Toast.LENGTH_SHORT).show();
                    }
                }
                // create new note
                else {
                    Note note = new Note();
                    note.setTitle(stitle);
                    note.setContent(scontent);

                    long result = myDB.Insert(note);
                    if (result > 0) {
                        Toast.makeText(getApplicationContext(), "Created", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if empty note then simply go back
                if (id == -1) {
                    finish();
                }
                else {
                    int result = myDB.Delete(id);
                    if (result > 0) {
                        Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go back
                finish();
            }
        });
    }


    private void HighlightText(SpannableString titleSpan, String searchText) {
        String lowerText = titleSpan.toString().toLowerCase();
        String lowerSearchText = searchText.toLowerCase();
        int index = lowerText.indexOf(lowerSearchText);
        while (index >=0) {
            int end = index + lowerSearchText.length();
            titleSpan.setSpan(new BackgroundColorSpan(getResources().getColor(R.color.orange)),
                    index, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            index = lowerText.indexOf(lowerSearchText, end);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect rect = new Rect();
                search.getGlobalVisibleRect(rect);
                if (!rect.contains( (int) ev.getRawX(), (int) ev.getRawY())) {
                    search.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myDB != null) {
            myDB.close();
        }
    }
}