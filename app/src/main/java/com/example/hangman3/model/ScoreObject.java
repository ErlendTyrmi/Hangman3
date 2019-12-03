package com.example.hangman3.model;

import java.sql.Timestamp;

public class ScoreObject implements Comparable<ScoreObject> {
    private String name = "Player Name";
    private int score;
    private Timestamp date;

    public ScoreObject(String name, int score) {
        this.name = name;
        this.score = score;
        // Timestamp set implicitly
        date = new Timestamp(System.currentTimeMillis());
    }

    public String getName() {
        return name;
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
        return "Player: " + this.getName() + "\t ScoreObject: " + this.getScore() + "\tDate: " + this.getDate();
    }

    @Override
    public int compareTo(ScoreObject s) {
        return this.getScore() - s.getScore();
    }
}
