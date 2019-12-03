package com.example.hangman3;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.EditText;
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
    private EditText enterLetter;
    private TextView secretWord, wrongLetters, scoreTextView, streakTextView;
    private DrawerLayout drawerLayoutMain;
    private Executor executor;
    private SoundPlayer soundPlayer;
    private ScoreFragment scoreFragment;
    private RecyclerView scoreRecyclerView;

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
        // Model (and score observer)
        soundPlayer = new SoundPlayer(this.getApplicationContext());
        game = ViewModelProviders.of(this).get(GameViewModel.class);
        // Import dictionaries. GameViewModel is started from end of this.
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
        //setScoreBoard();
        game.startRound(); // Round means guessing a single word
        secretWord.setText(game.getShownSecretWord());

        //soundPlayer.play("birds");

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
        // Get letter from EditText
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
            updateScoreBoard();
        } else {
            Toast.makeText(this, getResources().getString(R.string.gameover), Toast.LENGTH_LONG).show();
            secretWord.setText(game.getSecretWord());
            game.updateScore(false);
        }
        executor.execute(() -> {
            exportGameData();
            //setScoreBoard();
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

    private void updateScoreBoard() {
        GameDataObject data = game.getGameData();
        hiScoreListAdapter.setHiScores(data.getHiScores());
        scoreTextView.setText(Integer.toString(data.getCurrentScore()));
        streakTextView.setText(Integer.toString(data.getStreak()));
    }

    private void importGameData() {
        game.importData();
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


