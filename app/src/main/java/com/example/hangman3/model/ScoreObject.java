package com.example.hangman3.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class ScoreObject implements Comparable<ScoreObject>, Serializable {
    private String name = "Unknown Player";
    private int score;
    private Timestamp date;

    public ScoreObject(String name, int score) {
        this.name = name;
        this.score = score;
        // Timestamp set implicitly
        date = new Timestamp(System.currentTimeMillis());
    }

    public String getName() {

        if (name != null) {
            return name;
        } else {
            return "Ukendt Spiller";
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Player: " + this.getName() + " Score: " + this.getScore() + " Date: " + this.getDate();
    }

    @Override
    public int compareTo(ScoreObject s) {
        return Integer.compare(s.getScore(), this.getScore());
    }
}
