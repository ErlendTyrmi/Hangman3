package com.example.hangman3;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ScoreFragment extends Fragment {
    public ScoreFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ScoreFragment newInstance(String param1, String param2) {
        ScoreFragment fragment = new ScoreFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_score, container, false);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public void setScore(int n){
        TextView textView = getView().findViewById(R.id.textViewScore);
        String t = Integer.toString(n);
        textView.setText(t);
    }

    public void setStreak(int n){
        TextView textView = getView().findViewById(R.id.textViewStreak);
        String t = Integer.toString(n);
        textView.setText(t);
    }

    public void setHighScore(int n){
        TextView textView = getView().findViewById(R.id.textViewHighScore);
        String t = Integer.toString(n);
        textView.setText(t);
    }
}
