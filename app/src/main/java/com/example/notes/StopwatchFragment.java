package com.example.notes;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StopwatchFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 * fragments live in activities or other fragments
 * create a fragment and then link to activities
 * fragments communicate through activities
 * if activity is killed, fragments are also killed
 *
 * to place a fragment in an activity or other fragment, use fragment view container in activity
 * fragment is same as fragment view container but a legacy component - not suggested
 * fragment manager finds the fragments held in an activity
 */
public class StopwatchFragment extends Fragment {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment and return it
        View v =  inflater.inflate(R.layout.fragment_stopwatch, container, false);

        // interact with fragment layout using a function
        myfunction(v);

        return v;
    }

    private void myfunction(View v) {
        // getActivity() automatically finds the activity that holds this fragment
        MainActivity activity = (MainActivity) getActivity();
    }
}