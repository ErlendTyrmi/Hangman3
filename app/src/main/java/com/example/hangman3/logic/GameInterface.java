package com.example.hangman3.logic;

import java.util.ArrayList;
import java.util.HashMap;

public interface GameInterface {
    // Interface for the apps game com.example.hangman3.logic

    // Get dictionaries from the world wide web
    void importDictionaries() throws Exception;

    // Setup list of words to choose from, and choose secret word
    void setDictionary(int dictionaryId) throws Exception;

    HashMap<Integer, ArrayList<String>> getDictionaries();

    int getCurrentDictionaryID();

    // Setup the word to guess
    void startRound();

    int getScore();

    void setScore(int score);

    // Number of rounds currently won in a row
    int getStreakCount();

    // Number of times won in a row
    void setStreakCount(int count);

    // All time high score on local storage
    int getHighScore();

    void setHighScore(int score);

    int getNumberOfWrongGuesses();

    boolean isFinished();

    boolean isWon();

    // Check new letter entered
    boolean validateLetter(String letter);

    // Get the secret word formatted to hide letters
    String getShownSecretWord();

    // Get the whole word to display to loser
    String getSecretWord();

    // Get a string with all wrong letters guessed
    String getUsedWrongLetters();

    // Check that input is a valid letter
    boolean isALetter(String letter);

    // Check if letter is a duplicate entry
    boolean isLetterAlreadyGuessed(String letter);

    // Update score and return streak count and highscore
    int[] updateScore(boolean win);

}
