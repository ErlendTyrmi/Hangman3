package com.example.hangman3;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import com.example.hangman3.logic.Game;
import com.example.hangman3.logic.GameDataWriter;
import com.example.hangman3.logic.GameInterface;
import com.example.hangman3.logic.ThreadPerTaskExecutor;

import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {
    private GameInterface game = Game.getGame();
    private GameDataWriter gameDataWriter;
    private ImageButton toggleSettings;
    private ImageView gameImage;
    private TextView secretWord, wrongLetters;
    private EditText enterText;
    private ScoreFragment scoreBoardFragment;
    private DrawerLayout drawerLayoutMain;
    private Executor executor;
    private final String dataFileName = "gameData.txt";

    @Override
    protected void onRestart() {
        super.onRestart();
        runGame();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gameDataWriter = new GameDataWriter(game, this.getApplicationContext());
        runGame();
    }

    protected void runGame() {

        // View elements
        secretWord = this.findViewById(R.id.secretWord);
        gameImage = this.findViewById(R.id.gameImage);
        enterText = this.findViewById(R.id.enterText);
        wrongLetters = this.findViewById(R.id.wrongLetters);
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
            enterText.setHint("_");
            enterLetter();
            if (drawerLayoutMain.isDrawerOpen(GravityCompat.START)) {
                drawerLayoutMain.closeDrawer(GravityCompat.START, true);
            }
            return true;
        });

        // Toggle-button for Settings
        toggleSettings.setOnClickListener(v -> {
            Intent intent = new Intent(this, SettingsActivity.class);
            intent.putExtra("currentDictionaryID", game.getCurrentDictionaryID());
            startActivity(intent);
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
            wrongLetters.setText(game.getUsedWrongLetters().toUpperCase());
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
            gameDataWriter.storeGameData(data);
        } else {
            Toast.makeText(this, "Spillet er slut. Prøv igen!", Toast.LENGTH_LONG).show();
            game.setStreakCount(0);
            game.setScore(0);
            gameDataWriter.storeGameData(new int[]{0, game.getHighScore()});

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
        int[] gameData = gameDataWriter.readGameData();
        game.setStreakCount(gameData[0]);
        game.setHighScore(gameData[1]);
    }

    private void setScoreBoard() {
        scoreBoardFragment.setScore(game.getScore());
        scoreBoardFragment.setStreak(game.getStreakCount());
        scoreBoardFragment.setHighScore(game.getHighScore());
    }
}


