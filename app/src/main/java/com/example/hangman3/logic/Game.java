package com.example.hangman3.logic;

import com.example.hangman3.logic.dtuLogic.Galgelogik;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Game implements GameInterface {

    private Galgelogik galgelogik = new Galgelogik();
    private int streakCount, highScore, score;

    @Override
    public void setDictionary(int dictionaryId, String difficultyNumber) {
        try {
            switch (dictionaryId) {
                case 1:
                    galgelogik.hentOrdFraRegneark(difficultyNumber);
                    // "1" er nem "3" er svær. Kan kombineres som "12"
                    break;
                case 2:
                    galgelogik.hentOrdFraDr();
                    break;
                default:
                    System.out.println("Using the default dictionary.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Using the default dictionary.");
        }
    }

    @Override
    public void startNewGame() {
        galgelogik.nulstil();
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
    public int[] updateScoreOnWin() {
        streakCount++;
        score += galgelogik.getOrdet().length();

        if (score > highScore) {
            highScore = score;
        }
        return new int[]{streakCount, highScore};
    }
}


