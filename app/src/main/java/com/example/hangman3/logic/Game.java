package com.example.hangman3.logic;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

public class Game implements GameInterface {
    private static GameInterface game = null;
    private final String TAG = "Game";

    private Galgelogik galgelogik = new Galgelogik();
    private int streakCount, highScore, score, currentDictionaryId = 0; // default 0.
    private ArrayList<String> dictionary;
    private HashMap<Integer, ArrayList<String>> dictionaries = new HashMap<>();

    public static GameInterface getGame() {
        if (game == null) {
            game = new Game();
        }
        return game;
    }

    public void importDictionaries() throws Exception {
        // Setting a method local Galgelogik to avoid pollution of ongoing game
        Galgelogik importGalgelogik = new Galgelogik();

        // No network needed for first dictionary.
        dictionaries.put(0, listClone(importGalgelogik.muligeOrd));
        Log.d(TAG, "importDictionaries: imported dict: 0. First word: " + dictionaries.get(0));


        importGalgelogik.hentOrdFraRegneark("1");
        dictionaries.put(1, listClone(importGalgelogik.muligeOrd));
        Log.d(TAG, "importDictionaries: imported dict: 1 (DTU). First word: " + dictionaries.get(1));

        importGalgelogik.hentOrdFraRegneark("3");
        dictionaries.put(2, listClone(importGalgelogik.muligeOrd));
        Log.d(TAG, "importDictionaries: imported dict: 2 (DTU2). First word: " + dictionaries.get(2));

        importGalgelogik.hentOrdFraDr();
        dictionaries.put(3, listClone(importGalgelogik.muligeOrd));
        Log.d(TAG, "importDictionaries: imported dict: 3 (DR). First word: " + dictionaries.get(3));

        System.out.println("Printing all dictionaries:");
        for (int i = 0; i < 4; i++) {
            System.out.println("Dictionary " + i + ": " + dictionaries.get(i).toString());
        }

        setDictionary(currentDictionaryId);
    }

    public HashMap<Integer, ArrayList<String>> getDictionaries() {
        // Used for testing if dictionaries are downloaded properly
        return dictionaries;
    }

    @Override
    public void setDictionary(int dictionaryId) throws Exception {

        Log.d("Game", "Set dictionary called with id " + dictionaryId);

        this.currentDictionaryId = dictionaryId;


        switch (dictionaryId) {
            case 1:
                // DTU easy
                dictionary = dictionaries.get(1);
                break;
            case 2:
                // DTU hard
                dictionary = dictionaries.get(2);
                break;
            case 3:
                // DR
                dictionary = dictionaries.get(3);
                break;
            default:
                // Default dictionary
                dictionary = dictionaries.get(0);
                System.out.println("Using the default dictionary.");
        }

        // Set dictionary in underlying logic "galgelogik"
        System.out.println("Setting this dictionary: " + dictionary);
        galgelogik.muligeOrd = dictionary;
        galgelogik.nulstil();
    }

    public int getCurrentDictionaryID() {
        return currentDictionaryId;
    }

    @Override
    public void startRound() {
        // Protection against too long words
        do {
            galgelogik.nulstil();
            System.out.println(galgelogik.muligeOrd);
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

    private ArrayList<String> listClone(ArrayList<String> list) {
        // A simple method for cloning arraylists.
        ArrayList<String> newList = new ArrayList<>();
        for (String s : list) {
            newList.add(s);
        }
        return newList;
    }
}


