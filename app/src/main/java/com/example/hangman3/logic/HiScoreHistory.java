package com.example.hangman3.logic;

import java.util.ArrayList;
import java.util.Collections;

public class HiScoreHistory {
    private static final int NumOfHiScores = 5;
    private ArrayList<Score> hiScores;

    public ArrayList<Score> getHiScores() {
        return hiScores;
    }

    public boolean isNewHighScore(Score newScore) {
        return newScore.compareTo(hiScores.get(0)) > 0;
    }

    public boolean isTopNScore(Score newScore) {
        for (Score oldScore : hiScores) {
            if (newScore.compareTo(oldScore) > 0) {
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
}
