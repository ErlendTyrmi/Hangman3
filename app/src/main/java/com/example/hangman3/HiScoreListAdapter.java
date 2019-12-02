package com.example.hangman3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hangman3.logic.Score;

import java.util.List;

// Todo find out why vireholder is red?
public class HiScoreListAdapter extends RecyclerView.Adapter<HiScoreListAdapter.ViewHolder> {
    private List<Score> hiScores;
    private LayoutInflater layoutInflater;

    public HiScoreListAdapter(Context context, List<Score> hiScores) {
        this.layoutInflater = LayoutInflater.from(context);
        this.hiScores = hiScores;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.hiscore_recycler_row, parent, false);
        return new RecyclerView.ViewHolder(view);
    }

    @NonNull
    @Override
    public HiScoreListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull HiScoreListAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
