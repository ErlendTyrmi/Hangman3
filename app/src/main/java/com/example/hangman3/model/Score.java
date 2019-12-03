package com.example.hangman3.model;

import java.sql.Timestamp;

public class Score implements Comparable<Score> {
    private String name = "Player Name";
    private int score;
    private Timestamp date;

    public Score(String name, int score) {
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
        return "Player: " + this.getName() + "\t Score: " + this.getScore() + "\tDate: " + this.getDate();
    }

    @Override
    public int compareTo(Score s) {
        return this.getScore() - s.getScore();
    }
}
