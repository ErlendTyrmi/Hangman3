package com.example.hangman3;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hangman3.model.ScoreObject;

import java.util.ArrayList;
import java.util.List;

public class HiScoreListAdapter extends RecyclerView.Adapter<HiScoreListAdapter.ScoreViewHolder> {
    // https://codinginflow.com/tutorials/android/room-viewmodel-livedata-recyclerview-mvvm/part-6-recyclerview-adapter

    private List<ScoreObject> scoreList = new ArrayList<>();

    @NonNull
    @Override
    public ScoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View scoreView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.hiscore_recycler_row, parent, false);
        return new ScoreViewHolder(scoreView);
    }

    @Override
    public void onBindViewHolder(@NonNull ScoreViewHolder holder, int position) {
        ScoreObject currentScore = scoreList.get(position);
        holder.name.setText(currentScore.getName());
        holder.score.setText(currentScore.getScore());
        holder.date.setText(currentScore.getDate().toString().substring(0, 10));
    }

    @Override
    public int getItemCount() {
        if (scoreList == null) {
            return 0;
        }
        return scoreList.size();
    }

    public void setHiScores(List<ScoreObject> scoreList) {
        this.scoreList = scoreList;
    }

    class ScoreViewHolder extends RecyclerView.ViewHolder {
        private TextView name,
                score,
                date;

        public ScoreViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.list_name);
            score = itemView.findViewById(R.id.list_score);
            date = itemView.findViewById(R.id.list_date);
        }
    }



}
