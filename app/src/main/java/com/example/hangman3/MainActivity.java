package com.example.hangman3;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import com.example.hangman3.logic.Game;
import com.example.hangman3.logic.GameData;
import com.example.hangman3.logic.GameInterface;
import com.example.hangman3.logic.ThreadPerTaskExecutor;

import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {
    private GameInterface game;
    private GameData gameData;
    private ImageButton settingsButton;
    private ImageView gameImage;
    private TextView secretWord, wrongLetters;
    private EditText enterLetter;
    private ScoreFragment scoreBoardFragment;
    private DrawerLayout drawerLayoutMain;
    private Executor executor;
    private ProgressBar progressBar;
    private SoundPlayer soundPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // View elements
        secretWord = this.findViewById(R.id.secretWord);
        gameImage = this.findViewById(R.id.gameImage);
        enterLetter = this.findViewById(R.id.enterLetter);
        wrongLetters = this.findViewById(R.id.wrongLetters);
        settingsButton = this.findViewById(R.id.settingsButton);
        // Confusing: is not a drawer, but children can be drawers
        drawerLayoutMain = this.findViewById(R.id.scoreBoardDrawerLayout);
        executor = new ThreadPerTaskExecutor();
        // For manipulating views in scoreboard
        FragmentManager fm = getSupportFragmentManager();
        scoreBoardFragment = (ScoreFragment) fm.findFragmentById(R.id.fragmentL);
        soundPlayer = new SoundPlayer(this.getApplicationContext());
        game = Game.getGame();
        progressBar = this.findViewById(R.id.progressBar);
        // Import dictionaries. Game is started from end of this.
        new DictionaryImporter().execute();
        gameData = new GameData(game, this.getApplicationContext());
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //runGame();
        resetView();
    }

    protected void runGame() {

        // Get saved game data from disk when starting up
        importGameData();
        setScoreBoard();
        game.startRound(); // Round means guessing a single word
        secretWord.setText(game.getShownSecretWord());

        soundPlayer.play("birds");

        // Enter as go-button, keep keyboard up
        enterLetter.setOnEditorActionListener((v, actionId, event) -> {
            enterLetter.setHint("_");
            handleEnterLetter();
            if (drawerLayoutMain.isDrawerOpen(GravityCompat.START)) {
                drawerLayoutMain.closeDrawer(GravityCompat.START, true);
            }
            return true;
        });

        // Toggle-button for Settings
        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        });
    }

    private void handleEnterLetter() {

        String letter = enterLetter.getText().toString().toUpperCase();
        enterLetter.setText(letter);

        if (!game.isALetter(letter)) {
            Toast.makeText(this, getResources().getString(R.string.noletter), Toast.LENGTH_SHORT).show();

        } else if (game.isLetterAlreadyGuessed(letter)) {
            Toast.makeText(this, getResources().getString(R.string.tryotherletter), Toast.LENGTH_SHORT).show();

        } else if (game.validateLetter(letter)) {
            secretWord.setText(game.getShownSecretWord());

        } else {

            // Handle sounds
            int numberOfWrongGuesses = game.getNumberOfWrongGuesses();

            if (numberOfWrongGuesses >= 6) {
                soundPlayer.play("crack");

            } else if (numberOfWrongGuesses == 5) {
                soundPlayer.play("rope");

            } else {
                soundPlayer.play("birds");
            }
        }
        new Handler().postDelayed(() -> {
            updateImage(game.getNumberOfWrongGuesses());
            wrongLetters.setText(game.getUsedWrongLetters().toUpperCase());
            if (game.isFinished()) {
                showResult();
            }
            enterLetter.setText("");
        }, 500);
    }

    private void showResult() {
        int[] data;
        if (game.isWon()) {
            Toast.makeText(this, getResources().getString(R.string.correctletter), Toast.LENGTH_LONG).show();
            gameImage.setImageResource(R.drawable.hangmanwin);
            // Update points and store data
            data = game.updateScore(true);
        } else {
            Toast.makeText(this, getResources().getString(R.string.gameover), Toast.LENGTH_LONG).show();
            secretWord.setText(game.getSecretWord());
            game.setStreakCount(0);
            game.setScore(0);
            data = game.updateScore(false);
        }
        executor.execute(() -> {
            gameData.storeGameData(data);
            setScoreBoard();
        });

        new Handler().postDelayed(this::resetView, 3000);
    }

    private void resetView() {
        game.startRound();
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

    private class DictionaryImporter extends AsyncTask<Void, Boolean, Boolean> {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                game.importDictionaries();
            } catch (Exception e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean works) {
            if (!works) {
                Toast.makeText(getApplicationContext(), "Ingen netforbindelse", Toast.LENGTH_LONG).show();
            }
            progressBar.setVisibility(View.INVISIBLE);
            runGame();
            // Import should happen in background on startup, so default dictionary is enforced.
        }
    }

    private void importGameData() {
        int[] gameData = this.gameData.readGameData();
        game.setScore(gameData[0]);
        game.setStreakCount(gameData[1]);
        game.setHighScore(gameData[2]);
    }

    private void setScoreBoard() {
        scoreBoardFragment.setScore(game.getScore());
        scoreBoardFragment.setStreak(game.getStreakCount());
        scoreBoardFragment.setHighScore(game.getHighScore());
    }
}


