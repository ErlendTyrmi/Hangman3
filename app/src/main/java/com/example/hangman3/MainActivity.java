package com.example.hangman3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.hangman3.logic.Game;
import com.example.hangman3.logic.GameInterface;
import com.google.android.gms.common.api.Api;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.sql.SQLOutput;
import java.util.Arrays;
import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity implements SettingsFragment.OnFragmentInteractionListener {
    private GameInterface game = new Game();
    private ImageButton toggleScoreBoard, toggleSettings, setDefaultDictionary, setDrDictionary, setDTU1Dictionary, setDTU2Doctionary;
    private ImageView gameImage;
    private TextView secretWord, wrongLetters;
    private EditText enterText;
    private ScoreFragment scoreBoardFragment;
    private DrawerLayout drawerLayoutMain;
    private Executor executor;
    private final String dataFileName = "gameData.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // View elements
        secretWord = this.findViewById(R.id.secretWord);
        gameImage = this.findViewById(R.id.gameImage);
        enterText = this.findViewById(R.id.enterText);
        wrongLetters = this.findViewById(R.id.wrongLetters);
        //toggleScoreBoard = this.findViewById(R.id.scoreButton);
        toggleSettings = this.findViewById(R.id.settingsButton);
        // Buttons from settings

        // Confusing: is not a drawer, but children can be drawers
        drawerLayoutMain = this.findViewById(R.id.scoreBoardDrawerLayout);
        executor = new ThreadPerTaskExecutor();
        // For manipulating views in fragments
        FragmentManager fm = getSupportFragmentManager();
        scoreBoardFragment = (ScoreFragment) fm.findFragmentById(R.id.fragmentL);

        // Get saved game data when starting up
        importGameData();
        setScoreBoard();

        executor.execute(() -> {
            game.setDictionary(0, "none");
            game.startNewGame();
            secretWord.setText(game.getShownSecretWord());
        });

        executor.execute(() -> {
            MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.birds);
            mediaPlayer.start();
        });

        // Enter as go-button, keep keyboard up
        enterText.setOnEditorActionListener((v, actionId, event) -> {
            enterLetter();
            if (drawerLayoutMain.isDrawerOpen(GravityCompat.START)){
                drawerLayoutMain.closeDrawer(GravityCompat.START, true);
            }
            return true;
        });

        //enterText.onKeyUp();

        // Toggle-button for Scoreboard
        //toggleScoreBoard.setOnClickListener(v -> {
        //    drawerLayoutMain.openDrawer(GravityCompat.START, true);
        //});

        // Toggle-button for Scoreboard
        toggleSettings.setOnClickListener(v -> {
            drawerLayoutMain.openDrawer(GravityCompat.END, true);
        });
    }

    private void enterLetter() {

        String letter = enterText.getText().toString().toUpperCase();
        enterText.setText(letter);

        if (!game.isALetter(letter)) {
            Toast.makeText(this, "'" + letter + "' er ikke et bogstav!", Toast.LENGTH_SHORT).show();

        } else if (game.isLetterAlreadyGuessed(letter)) {
            Toast.makeText(this, "Prøv et andet bogstav!", Toast.LENGTH_SHORT).show();

        } else if (game.validateLetter(letter)) {
            secretWord.setText(game.getShownSecretWord());

        } else {

            // Handle sounds
            int numberOfWrongGuesses = game.getNumberOfWrongGuesses();
            if (numberOfWrongGuesses >= 6) {
                MediaPlayer mediaPlayerCrack = MediaPlayer.create(this, R.raw.crack);
                executor.execute(mediaPlayerCrack::start);
            } else if (numberOfWrongGuesses == 5) {
                MediaPlayer mediaPlayerRope = MediaPlayer.create(this, R.raw.rope);
                executor.execute(mediaPlayerRope::start);
            } else {
                MediaPlayer mediaPlayerBirds = MediaPlayer.create(this, R.raw.birds);
                executor.execute(mediaPlayerBirds::start);

            }
        }
        new Handler().postDelayed(() -> {
            updateImage(game.getNumberOfWrongGuesses());
            wrongLetters.setText(game.getUsedWrongLetters());
            if (game.isFinished()) {
                checkWin();
            }
            enterText.setText("");
        }, 500);
    }

    private void checkWin() {
        // TODO: Check if this can be refactored into game class
        if (game.isWon()) {
            Toast.makeText(this, "RIGTIG GÆT!", Toast.LENGTH_LONG).show();
            gameImage.setImageResource(R.drawable.hangmanwin);
            // Update points and store data
            int[] data = game.updateScoreOnWin();
            storeGameData(data);
        } else {
            Toast.makeText(this, "Spillet er slut. Prøv igen!", Toast.LENGTH_LONG).show();
            game.setStreakCount(0);
            game.setScore(0);
            storeGameData(new int[]{0, game.getHighScore()});

        }
        importGameData();
        setScoreBoard();
        new Handler().postDelayed(this::resetView, 3000);
    }

    private void resetView() {
        game.startNewGame();
        updateImage(game.getNumberOfWrongGuesses());
        wrongLetters.setText(game.getUsedWrongLetters());
        secretWord.setText(game.getShownSecretWord());
        drawerLayoutMain.openDrawer(GravityCompat.START, true);
    }

    private void updateImage(int wrongGuesses) {
        switch (wrongGuesses) {
            case 0:
                gameImage.setImageResource(R.drawable.hangman0);
                break;
            case 1:
                gameImage.setImageResource(R.drawable.hangman1);
                break;
            case 2:
                gameImage.setImageResource(R.drawable.hangman2);
                break;
            case 3:
                gameImage.setImageResource(R.drawable.hangman3);
                break;
            case 4:
                gameImage.setImageResource(R.drawable.hangman4);
                break;
            case 5:
                gameImage.setImageResource(R.drawable.hangman5);
                break;
            case 6:
                gameImage.setImageResource(R.drawable.hangman6);
                break;
        }
    }

    private void importGameData() {
        int[] gameData = readGameData();
        game.setStreakCount(gameData[0]);
        game.setHighScore(gameData[1]);
    }

    private void storeGameData(int[] data) {

        String input = Arrays
                .stream(data)
                .mapToObj(String::valueOf)
                .reduce((a, b) -> a.concat(",").concat(b))
                .get();

        Log.d("Storing data", "Data from game, converted to string: " + input);

        // New thread
        executor.execute(() -> {
            try (FileOutputStream outputStream = openFileOutput(
                    dataFileName, MODE_PRIVATE)) {
                outputStream.write(input.getBytes());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private int[] readGameData() {
        int[] data = new int[2];
        // New thread TODO: Maybe set variables from here? The array does not change.
        executor.execute(() -> {
            try (FileInputStream fileInputStream = openFileInput(dataFileName)) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
                String dataString = bufferedReader.readLine();
                String[] dataStringArray = dataString.split(",");
                data[0] = Integer.parseInt(dataStringArray[0]);
                data[1] = Integer.parseInt(dataStringArray[1]);
                Log.d("ReadGameData", "Imported data from file: " + data[0] + " and " + data[1]);
            } catch (FileNotFoundException f) {
                Log.e("readGameData", "Could not find a data file.");
                f.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        try {
            // Sleep to return updated data
            Thread.sleep(1000);
            Log.d("ReadGameData", "Returning data from file: " + data[0] + " and " + data[1]);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return data;
    }

    private void setScoreBoard() {
        scoreBoardFragment.setScore(game.getScore());
        scoreBoardFragment.setStreak(game.getStreakCount());
        scoreBoardFragment.setHighScore(game.getHighScore());
    }

    @Override
    public void onFragmentInteraction(int dictionary) {

        switch(dictionary){
            case 0:
                game.setDictionary(0, "");
                break;
            case 1:
                game.setDictionary(1, "23");
                break;
            case 2:
                game.setDictionary(2, "");
                break;
            default:
        }

        Toast.makeText(this, dictionary + " er valgt.", Toast.LENGTH_SHORT).show();
    }
}


