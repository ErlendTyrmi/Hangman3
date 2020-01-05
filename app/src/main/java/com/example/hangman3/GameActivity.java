package com.example.hangman3;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hangman3.model.GameDataObject;
import com.example.hangman3.model.GameViewModel;
import com.example.hangman3.model.ThreadPerTaskExecutor;

import java.util.concurrent.Executor;

public class GameActivity extends AppCompatActivity {
    HiScoreListAdapter hiScoreListAdapter;
    private final String TAG = "GameActivity";
    private ImageButton settingsButton;
    private ImageView gameImage;
    private GameViewModel game;
    private final String[] KEYS = {
            "a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
            "k", "l", "m", "n", "o", "p", "q", "r", "s", "t",
            "u", "v", "w", "x", "y", "z", "æ", "ø", "å"
    };
    private TextView secretWord, wrongLetters, scoreTextView, streakTextView;
    private DrawerLayout drawerLayoutMain;
    private Executor executor;
    private SoundPlayer soundPlayer;
    private ScoreFragment scoreFragment;
    private TextView enterLetter;
    private RecyclerView scoreRecyclerView;
    private KeyboardFragment keyboardFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // View elements
        secretWord = this.findViewById(R.id.secretWord);
        gameImage = this.findViewById(R.id.gameImage);
        enterLetter = this.findViewById(R.id.enterLetter);
        wrongLetters = this.findViewById(R.id.wrongLetters);
        settingsButton = this.findViewById(R.id.settingsButton);
        drawerLayoutMain = this.findViewById(R.id.mainDrawerLayout);
        executor = new ThreadPerTaskExecutor();
        // ScoreFragment
        FragmentManager fm = getSupportFragmentManager();
        scoreFragment = (ScoreFragment) fm.findFragmentById(R.id.scoreFragment);
        scoreTextView = findViewById(R.id.textViewScore);
        streakTextView = findViewById(R.id.textViewStreak);
        // Scoreboard RecyclerView
        scoreRecyclerView = findViewById(R.id.hiScoreRecyclerView);
        scoreRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        scoreRecyclerView.setHasFixedSize(true);
        hiScoreListAdapter = new HiScoreListAdapter();
        scoreRecyclerView.setAdapter(hiScoreListAdapter);
        // Keyboard
        keyboardFragment = (KeyboardFragment) fm.findFragmentById(R.id.keyboardFragment);

        // Model
        soundPlayer = new SoundPlayer(this.getApplicationContext());
        game = ViewModelProviders.of(this).get(GameViewModel.class);
        new DictionaryImporter().execute();
        runGame();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        resetView();
    }

    protected void runGame() {

        // Get saved game data from disk when starting up
        importGameData();
        // TODO update score on start
        game.startRound(); // Round means guessing a single word
        secretWord.setText(game.getShownSecretWord());

        // Toggle-button for Settings
        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        });
    }

    public void keyClicked(View v) {
        String key = "";
        // Giant if-else for ALL keys
        if (v.getId() == R.id.button) {
            key = KEYS[0];
        } else if (v.getId() == R.id.button2) {
            key = KEYS[1];
        } else if (v.getId() == R.id.button3) {
            key = KEYS[2];
        } else if (v.getId() == R.id.button4) {
            key = KEYS[3];
        } else if (v.getId() == R.id.button5) {
            key = KEYS[4];
        } else if (v.getId() == R.id.button6) {
            key = KEYS[5];
        } else if (v.getId() == R.id.button7) {
            key = KEYS[6];
        } else if (v.getId() == R.id.button8) {
            key = KEYS[7];
        } else if (v.getId() == R.id.button9) {
            key = KEYS[8];
        } else if (v.getId() == R.id.button10) {
            key = KEYS[9];
        } else if (v.getId() == R.id.button11) {
            key = KEYS[10];
        } else if (v.getId() == R.id.button12) {
            key = KEYS[11];
        } else if (v.getId() == R.id.button13) {
            key = KEYS[12];
        } else if (v.getId() == R.id.button14) {
            key = KEYS[13];
        } else if (v.getId() == R.id.button15) {
            key = KEYS[14];
        } else if (v.getId() == R.id.button16) {
            key = KEYS[15];
        } else if (v.getId() == R.id.button17) {
            key = KEYS[16];
        } else if (v.getId() == R.id.button18) {
            key = KEYS[17];
        } else if (v.getId() == R.id.button19) {
            key = KEYS[18];
        } else if (v.getId() == R.id.button20) {
            key = KEYS[19];
        } else if (v.getId() == R.id.button21) {
            key = KEYS[20];
        } else if (v.getId() == R.id.button22) {
            key = KEYS[21];
        } else if (v.getId() == R.id.button23) {
            key = KEYS[22];
        } else if (v.getId() == R.id.button24) {
            key = KEYS[23];
        } else if (v.getId() == R.id.button25) {
            key = KEYS[24];
        } else if (v.getId() == R.id.button26) {
            key = KEYS[25];
        } else if (v.getId() == R.id.button27) {
            key = KEYS[26];
        } else if (v.getId() == R.id.button28) {
            key = KEYS[27];
        } else if (v.getId() == R.id.button29) {
            key = KEYS[28];
        }

        Log.d(TAG, "keyClicked: " + key);
        handleEnterLetter(key);
    }

    private void handleEnterLetter(String letter) {

        enterLetter.setText(letter.toUpperCase());

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
        if (game.isWon()) {
            if (game.isNewHighScore()) {
                Toast.makeText(this, R.string.yousethighscore, Toast.LENGTH_LONG).show();
                // Big Confetti Show Here!
            } else {
                Toast.makeText(this, getResources().getString(R.string.correctletter), Toast.LENGTH_LONG).show();
            }
            gameImage.setImageResource(R.drawable.hangmanwin);
            // Update points and store data
            game.updateScore(true);
        } else {
            Toast.makeText(this, getResources().getString(R.string.gameover), Toast.LENGTH_LONG).show();
            secretWord.setText(game.getSecretWord());
            game.updateScore(false);
        }
        // Update the score board no matter if you win or lose.
        exportGameData();
        updateScoreBoard();

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

    private void updateScoreBoard() {
        GameDataObject data = game.getGameData();
        scoreTextView.setText(Integer.toString(data.getCurrentScore()));
        streakTextView.setText(Integer.toString(data.getStreak()));

        Log.d(TAG, "updateScoreBoard: Setting High Score List: " + game.getGameData().getHiScores());
        hiScoreListAdapter.setHiScores(game.getGameData().getHiScores());
    }

    private void importGameData() {
        game.importData();
        // Update from saved data every time
        updateScoreBoard();
    }

    private void exportGameData() {
        game.exportData();
    }

    private class DictionaryImporter extends AsyncTask<Void, Boolean, Boolean> {

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
        }
    }
}


