package com.example.hangman3.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class GameDataObject implements Serializable {
    private static final int NumOfHiScores = 5;
    private int streak = 0;
    private int currentScore = 0;
    private ArrayList<ScoreObject> hiScores = new ArrayList<>();

    public ArrayList<ScoreObject> getHiScores() {
        return hiScores;
    }

    public boolean isNewHighScore(int newScore) {
        return hiScores.size() == 0 || newScore > hiScores.get(0).getScore();
    }

    public boolean isTopNScore(int newScore) {
        if (hiScores != null && hiScores.size() > 4) {

            for (ScoreObject oldScore : hiScores) {
                if (newScore > oldScore.getScore()) {
                    return true;
                }
            }
            return false;
        } else {
            // If no list is found or the list is still shorter than 5:
            return true;
        }
    }

    public void addHiScore(ScoreObject newScore) {
        // Check if top score first!
        if (hiScores == null) {
            hiScores = new ArrayList<>();
        }
        hiScores.add(newScore);
        // Shorten the list so only the top "NumOfHiScores" are visible
        shortenHighScores(NumOfHiScores);
    }

    public String toString() {
        int i = 0;
        StringBuilder sb = new StringBuilder();
        sb.append("Score: " + currentScore);
        sb.append(" Streak: " + streak);

        for (ScoreObject s : hiScores) {
            sb.append(i + s.toString());
        }
        return sb.toString();
    }

    private void shortenHighScores(int length) {
        Collections.sort(hiScores);
        if (hiScores.size() > length) {
            hiScores.subList(length, hiScores.size()).clear();
        }
    }

    public void setStreak(int streak) {
        this.streak = streak;
    }

    public int getStreak() {
        return streak;
    }

    public int getCurrentScore() {
        return currentScore;
    }

    public void setCurrentScore(int currentScore) {
        this.currentScore = currentScore;
    }
}
