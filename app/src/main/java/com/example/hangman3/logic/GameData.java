package com.example.hangman3.logic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class GameData implements Serializable {
    private static final int NumOfHiScores = 5;
    private int streak = 0;
    private int currentScore = 0;
    private ArrayList<Score> hiScores;

    public ArrayList<Score> getHiScores() {
        return hiScores;
    }

    public boolean isNewHighScore(int newScore) {
        return newScore > hiScores.get(0).getScore();
    }

    public boolean isTopNScore(int newScore) {
        for (Score oldScore : hiScores) {
            if (newScore > oldScore.getScore()) {
                return true;
            }
        }
        // Else none was found
        return false;
    }

    public void addHiScore(Score newScore) {
        // Check if top score first!
        hiScores.add(newScore);
        // Shorten the list so only the top "NumOfHiScores" are visible
        shortenHighScores(NumOfHiScores);
    }

    public String toString() {
        int i = 0;
        StringBuilder sb = new StringBuilder();
        for (Score s : hiScores) {
            sb.append(i + s.toString());
        }
        return sb.toString();
    }

    private void shortenHighScores(int length) {
        Collections.sort(hiScores);
        hiScores.subList(length, hiScores.size()).clear();
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
