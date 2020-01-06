package com.example.hangman3.model;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.hangman3.Repositories.DataSerializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

public class GameViewModel extends AndroidViewModel {
    private final String TAG = "GameViewModel";
    private Galgelogik galgelogik;
    private GameDataObject gameData;
    private DataSerializer dataSerializer;
    private Dictionaries dictionaries;
    private String playerName;

    public GameViewModel(@NonNull Application application) {
        super(application);
        dataSerializer = new DataSerializer(application);
        galgelogik = new Galgelogik();
        dictionaries = Dictionaries.getDictionaryManager();
    }

    public void importDictionaries() throws Exception {
        // Setting a method local Galgelogik to avoid pollution of ongoing game
        Galgelogik importGalgelogik = new Galgelogik();

        // No network needed for first dictionary.
        dictionaries.addDictionary(0, listClone(importGalgelogik.muligeOrd));
        Log.d(TAG, "importDictionaries: imported dict: 0. First word: " + dictionaries.getDictionaries().get(0));


        importGalgelogik.hentOrdFraRegneark("1");
        dictionaries.addDictionary(1, listClone(importGalgelogik.muligeOrd));
        Log.d(TAG, "importDictionaries: imported dict: 1 (DTU). First word: " + dictionaries.getDictionaries().get(1));

        importGalgelogik.hentOrdFraRegneark("3");
        dictionaries.addDictionary(2, listClone(importGalgelogik.muligeOrd));
        Log.d(TAG, "importDictionaries: imported dict: 2 (DTU2). First word: " + dictionaries.getDictionaries().get(2));

        importGalgelogik.hentOrdFraDr();
        dictionaries.addDictionary(3, listClone(importGalgelogik.muligeOrd));
        Log.d(TAG, "importDictionaries: imported dict: 3 (DR). First word: " + dictionaries.getDictionaries().get(3));

        System.out.println("Printing all dictionaries:");
        for (int i = 0; i < 4; i++) {
            System.out.println("Dictionary " + i + ": " + dictionaries.getDictionaries().get(i).toString());
        }

        setDictionary(this.dictionaries.getId());
    }

    public HashMap<Integer, ArrayList<String>> getDictionaries() {
        // Used for testing if dictionaries are downloaded properly
        return dictionaries.getDictionaries();
    }

    public void setDictionary(int dictionaryId) {
        Log.d("GameViewModel", "Set dictionary called with id " + dictionaryId);
        dictionaries.setId(dictionaryId);

        switch (dictionaryId) {
            case 1:
                // DTU easy
                dictionaries.getDictionaries().get(1);
                break;
            case 2:
                // DTU hard
                dictionaries.getDictionaries().get(2);
                break;
            case 3:
                // DR
                dictionaries.getDictionaries().get(3);
                break;
            default:
                // Default dictionary
                dictionaries.getDictionaries().get(0);
                System.out.println("Using the default dictionary.");
        }

        // Set dictionary in underlying logic "galgelogik"
        System.out.println("Setting this dictionary: " +
                dictionaries.getDictionaries().get(dictionaryId));
        galgelogik.muligeOrd = dictionaries.getDictionaries().get(dictionaryId);
        galgelogik.nulstil();
    }

    public int getCurrentDictionaryID() {
        return dictionaries.getId();
    }

    public void startRound() {
        // Protection against too long words
        Log.d(TAG, "startRound: Current dictionary ID: " + dictionaries.getId());
        do {
            galgelogik.muligeOrd = dictionaries.getDictionary();
            galgelogik.nulstil();
        } while (galgelogik.getOrdet().length() > 16);
        Log.d(TAG, "startRound: (For test) Guess the word: " + galgelogik.getOrdet());
    }

    public int getScore() {
        return gameData.getCurrentScore();
    }

    public void setScore(int score) {
        gameData.setCurrentScore(score);
    }

    public int getStreakCount() {
        return gameData.getStreak();
    }

    public void setStreakCount(int streakCount) {
        gameData.setStreak(streakCount);
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public boolean isFinished() {
        // Check if round is over
        return galgelogik.getAntalForkerteBogstaver() > 5 || galgelogik.erSpilletVundet();
    }

    public boolean isNewHighScore() {
        return gameData.isNewHighScore(gameData.getCurrentScore());
    }

    public boolean isWon() {
        // Check if round is won
        return galgelogik.erSpilletVundet();
    }

    public boolean validateLetter(String letter) {
        galgelogik.gætBogstav(letter.toLowerCase());
        return galgelogik.erSidsteBogstavKorrekt();
    }

    public String getShownSecretWord() {
        String backendWord = galgelogik.getSynligtOrd().replace('*', '_');
        String word = "";
        for (int i = 0; i < backendWord.length(); i++) {
            word += backendWord.substring(i, i + 1) + ' ';
        }
        return word.substring(0, word.length() - 1);
    }

    public String getSecretWord() {
        return galgelogik.getOrdet();
    }

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

    public int getNumberOfWrongGuesses() {
        return galgelogik.getAntalForkerteBogstaver();
    }

    public boolean isALetter(String letter) {
        String regEx = "[A-ZÆØÅa-zæøå]";
        return Pattern.matches(regEx, letter);
    }

    public boolean isLetterAlreadyGuessed(String letter) {
        return galgelogik.getBrugteBogstaver().contains(letter.toLowerCase());
    }

    public void updateScore(boolean win) {
        int score = gameData.getCurrentScore();

        if (win) {
            Log.d(TAG, "updateScore: Game won.");

            // Add to streak
            int streak = gameData.getStreak() + 1;
            gameData.setStreak(streak);

            // Add to score (Shorter words give more points)
            score += 20 - galgelogik.getOrdet().length();

            gameData.setCurrentScore(score);

        } else {
            // Round is over and score is summed and stored
            Log.d(TAG, "updateScore: Game lost.");

            // Saving high score
            Log.d(TAG, "updateScore: Checking if score is top 5:");
            if (gameData.isTopNScore(score)) {
                Log.d(TAG, "updateScore: The score made the high score list.");
                ScoreObject newScore = new ScoreObject(playerName, score);
                gameData.addHiScore(newScore);
            }

            // Resetting score
            gameData.setStreak(0);
            gameData.setCurrentScore(0);
        }
    }

    public void importData() {
        try {
            gameData = dataSerializer.getGameData();
        } catch (Exception e) {
            // If gamedata is not created, create new
            gameData = new GameDataObject();
            Log.d(TAG, "importGameData: No gamedata found. Is this the first time running the program?");
            // e.printStackTrace();
        }
    }

    public void exportData() {
        // Saves data to disk
        try {
            dataSerializer.StoreGameData(gameData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public GameDataObject getGameData() {
        return gameData;
    }

    private ArrayList<String> listClone(ArrayList<String> list) {
        // A method for cloning ArrayLists.
        ArrayList<String> newList = new ArrayList<>();
        for (String s : list) {
            newList.add(s);
        }
        return newList;
    }
}


