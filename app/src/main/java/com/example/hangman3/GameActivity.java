package com.example.hangman3;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
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
    private final String[] LETTERS = {
            "a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
            "k", "l", "m", "n", "o", "p", "q", "r", "s", "t",
            "u", "v", "w", "x", "y", "z", "æ", "ø", "å"
    };
    private Button[] keys = new Button[29];
    private TextView secretWord, wrongLetters, scoreTextView, streakTextView;
    private DrawerLayout drawerLayoutMain;
    private Executor executor;
    private SoundPlayer soundPlayer;
    private ScoreFragment scoreFragment;
    private TextView enterLetter;
    private RecyclerView scoreRecyclerView;

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

        // Defining all 29 keyboard buttons, corresponding to letters
        keys[0] = findViewById(R.id.button);
        keys[1] = findViewById(R.id.button2);
        keys[2] = findViewById(R.id.button3);
        keys[3] = findViewById(R.id.button4);
        keys[4] = findViewById(R.id.button5);
        keys[5] = findViewById(R.id.button6);
        keys[6] = findViewById(R.id.button7);
        keys[7] = findViewById(R.id.button8);
        keys[8] = findViewById(R.id.button9);
        keys[9] = findViewById(R.id.button10);
        keys[10] = findViewById(R.id.button11);
        keys[11] = findViewById(R.id.button12);
        keys[12] = findViewById(R.id.button13);
        keys[13] = findViewById(R.id.button14);
        keys[14] = findViewById(R.id.button15);
        keys[15] = findViewById(R.id.button16);
        keys[16] = findViewById(R.id.button17);
        keys[17] = findViewById(R.id.button18);
        keys[18] = findViewById(R.id.button19);
        keys[19] = findViewById(R.id.button20);
        keys[20] = findViewById(R.id.button21);
        keys[21] = findViewById(R.id.button22);
        keys[22] = findViewById(R.id.button23);
        keys[23] = findViewById(R.id.button24);
        keys[24] = findViewById(R.id.button25);
        keys[25] = findViewById(R.id.button26);
        keys[26] = findViewById(R.id.button27);
        keys[27] = findViewById(R.id.button28);
        keys[28] = findViewById(R.id.button29);

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
        // Runs once every time the program starts.

        // Get saved game data from disk when starting up
        importGameData();
        game.startRound(); // Round means guessing a single word
        secretWord.setText(game.getShownSecretWord());

        // Toggle-button for Settings
        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        });
    }

    public void keyClicked(View v) {
        // Called on button click. Translates keyboard input to a letter
        String letter = "";
        Button key;

        // The arrays 'keys' and 'LETTERS' have corresponding indexes.
        for (int i = 0; i < keys.length; i++) {
            key = keys[i];
            if (v.getId() == key.getId()) {
                letter = LETTERS[i];
                key.setEnabled(false);
                break; // Stop looking
            }
        }
        Log.d(TAG, "keyClicked: " + letter);
        handleEnterLetter(letter);
    }

    private void handleEnterLetter(String letter) {
        // Handles actions based on the key entered.
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
        // Shows the result on screen and then stores progress.
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
        // Resets screen after each round (round = guessing one word).
        game.startRound();
        updateImage(game.getNumberOfWrongGuesses());
        wrongLetters.setText(game.getUsedWrongLetters());
        secretWord.setText(game.getShownSecretWord());
        drawerLayoutMain.openDrawer(GravityCompat.START, true);

        // Reset all disabled buttons
        for (Button b : keys) {
            b.setEnabled(true);
        }
    }

    private void updateImage(int wrongGuesses) {
        // Updates the main game image based on progress or result
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
        // Imports data from serialized file and stores as a GameDataObject.
        game.importData();
        updateScoreBoard(); // Update from saved data every time
    }

    private void exportGameData() {
        // Serialize GameDataObject as a file
        game.exportData();
    }

    private class DictionaryImporter extends AsyncTask<Void, Boolean, Boolean> {
        // Imports dictionaries at startup
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


