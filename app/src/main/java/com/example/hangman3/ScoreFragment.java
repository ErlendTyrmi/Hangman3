package com.example.hangman3;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ScoreFragment extends Fragment {
    private HiScoreListAdapter hiScoreListAdapter;

    public ScoreFragment() {
        // Required empty public constructor
    }

    public static ScoreFragment newInstance() {
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

        // Handle RecyclerView
        RecyclerView recyclerView = getActivity().findViewById(R.id.hiScoreList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        hiScoreListAdapter = new HiScoreListAdapter(this, hiScores); // TODO: How to get hiscores
        recyclerView.setAdapter(hiScoreListAdapter);

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

    // Todo: update score from game via MainActivity when this is showed
}
