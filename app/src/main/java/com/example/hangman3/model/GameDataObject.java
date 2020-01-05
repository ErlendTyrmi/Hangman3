package com.example.hangman3.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class GameDataObject implements Serializable {
    private static final int NUM_SCORES = 5;
    private int streak = 0;
    private int currentScore = 0;
    private ArrayList<ScoreObject> hiScores = new ArrayList<>();

    public ArrayList<ScoreObject> getHiScores() {
        shortenHighScores(NUM_SCORES); // Sort scores, and make sure it's the right length.
        return hiScores;
    }

    public boolean isNewHighScore(int newScore) {
        return hiScores.size() == 0 || newScore > hiScores.get(0).getScore();
    }

    public boolean isTopNScore(int newScore) {
        if (hiScores == null || hiScores.size() < 5) {
            return true;
        } else {
            for (ScoreObject oldScore : hiScores) {
                if (newScore > oldScore.getScore()) {
                    return true;
                }
            }
            // Default to false
            return false;
        }
    }

    public void addHiScore(ScoreObject newScore) {
        // Check if top score first!
        if (hiScores == null) {
            hiScores = new ArrayList<>();
        }
        hiScores.add(newScore);
        // Shorten the list so only the top "NUM_SCORES" are visible
        shortenHighScores(NUM_SCORES);
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
