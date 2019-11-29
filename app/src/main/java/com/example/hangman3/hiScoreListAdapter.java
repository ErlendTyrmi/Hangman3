package com.example.hangman3;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hangman3.logic.Score;

import java.util.List;

public class hiScoreListAdapter extends RecyclerView.Adapter {
    private List<Score> hiScores;
    private LayoutInflater layoutInflater;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.hiscore_recycler, parent, true);
        HiScoreViewHolder hiScoreViewholder = new HiScoreViewHolder(view);
        return hiScoreViewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Score score = hiScores.get(position);
    }

    @Override
    public int getItemCount() {
        return hiScores.size();
    }

    private class HiScoreViewHolder extends RecyclerView.ViewHolder {
        public HiScoreViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
