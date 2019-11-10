package com.example.hangman3.logic;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Game implements GameInterface {
    private static GameInterface game = null;
    private int streakCount, highScore, score, currentDictionaryId;

    private Galgelogik galgelogik = new Galgelogik();
    private ArrayList<String>[] dictionaries;
    private ArrayList<String> dictionary;

    public static GameInterface getGame() {
        if (game == null) {
            game = new Game();
        }
        return game;
    }

    public Game() {
        // Import online dictionaries at startup
        importDictionaries();
    }

    private void importDictionaries() {
        dictionaries = new ArrayList[4];
        try {
            // Reset muligeord.
            galgelogik = new Galgelogik();
            dictionaries[0] = galgelogik.muligeOrd;

            galgelogik.hentOrdFraRegneark("1");
            dictionaries[1] = galgelogik.muligeOrd;

            galgelogik.hentOrdFraRegneark("3");
            dictionaries[2] = galgelogik.muligeOrd;

            galgelogik.hentOrdFraDr();
            dictionaries[3] = galgelogik.muligeOrd;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setDictionary(int dictionaryId) {
        Log.d("Game", "Set dictionary called with id " + dictionaryId);
        this.currentDictionaryId = dictionaryId;
        try {
            switch (dictionaryId) {
                case 1:
                    // DTU easy
                    dictionary = dictionaries[1];
                    break;
                case 2:
                    // DTU hard
                    dictionary = dictionaries[2];
                    break;
                case 3:
                    // DR
                    dictionary = dictionaries[3];
                    break;
                default:
                    // Default dictionary
                    dictionary = dictionaries[0];
                    System.out.println("Using the default dictionary.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            // Try importing dictionaries again.
            importDictionaries();
            System.out.println("Using the default dictionary.");
        }
        // Set dictionary in underlying logic "galgelogik"
        galgelogik.muligeOrd = dictionary;
    }

    public int getCurrentDictionaryID() {
        return currentDictionaryId;
    }

    @Override
    public void startRound() {
        // Protection against too long words
        do {
            galgelogik.nulstil();
        } while (galgelogik.getOrdet().length() > 16);
    }

    @Override
    public int getScore() {
        return score;
    }

    @Override
    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public int getStreakCount() {
        return streakCount;
    }

    public void setStreakCount(int streakCount) {
        this.streakCount = streakCount;
    }

    @Override
    public int getHighScore() {
        return highScore;
    }

    @Override
    public void setHighScore(int newScore) {
        highScore = newScore;
    }

    @Override
    public boolean isFinished() {
        // Check if round is over
        return galgelogik.getAntalForkerteBogstaver() > 5 || galgelogik.erSpilletVundet();
    }

    @Override
    public boolean isWon() {
        // Check if round is won
        return galgelogik.erSpilletVundet();
    }

    @Override
    public boolean validateLetter(String letter) {
        galgelogik.gætBogstav(letter.toLowerCase());
        return galgelogik.erSidsteBogstavKorrekt();
    }

    @Override
    public String getShownSecretWord() {
        String backendWord = galgelogik.getSynligtOrd().replace('*', '_');
        String word = "";
        for (int i = 0; i < backendWord.length(); i++) {
            word += backendWord.substring(i, i + 1) + ' ';
        }
        return word.substring(0, word.length() - 1);
    }

    @Override
    public String getSecretWord() {
        return galgelogik.getOrdet();
    }

    @Override
    public String getUsedWrongLetters() {
        List<String> used = galgelogik.getBrugteBogstaver();
        List<String> wrong = new ArrayList<>();
        String secretWord = galgelogik.getOrdet();
        for (String s : used) {
            if (!secretWord.contains(s)) {
                wrong.add(s);
            }
        }
        secretWord = wrong.toString();
        return secretWord.substring(1, secretWord.length() - 1);
    }

    @Override
    public int getNumberOfWrongGuesses() {
        return galgelogik.getAntalForkerteBogstaver();
    }

    @Override
    public boolean isALetter(String letter) {
        String regEx = "[A-ZÆØÅa-zæøå]";
        return Pattern.matches(regEx, letter);
    }

    @Override
    public boolean isLetterAlreadyGuessed(String letter) {
        return galgelogik.getBrugteBogstaver().contains(letter.toLowerCase());
    }

    @Override
    public int[] updateScore(boolean win) {
        if (win) {
            streakCount++;
            score += galgelogik.getOrdet().length();
            if (score > highScore) {
                highScore = score;
            }
        } else {
            streakCount = 0;
            score = 0;
        }
        return new int[]{score, streakCount, highScore};
    }
}


