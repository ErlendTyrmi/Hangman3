package com.example.hangman3;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hangman3.logic.Game;
import com.example.hangman3.logic.GameInterface;

public class SettingsActivity extends AppCompatActivity {
    private final String TAG = "SettingsActivity";
    private RadioGroup radioGroup;
    private RadioButton defaultButton, dtuButton, drButton;
    private Switch switchToHardDTU;
    private TextView loading;
    private ImageButton backbutton;
    private GameInterface game = Game.getGame();
    private int dictionaryId;
    private boolean hardModeSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.activity_settings);

        radioGroup = findViewById(R.id.radioGroup);
        defaultButton = findViewById(R.id.radioButtonDefault);
        dtuButton = findViewById(R.id.radioButtonDTU);
        drButton = findViewById(R.id.radioButtonDR);
        switchToHardDTU = findViewById(R.id.switchToHardDTU);
        loading = findViewById(R.id.dictionaryLoadingTextView);
        backbutton = findViewById(R.id.backButton);

        int currentDictionaryID = intent.getIntExtra("currentDictionaryID", 0);
        Log.d(TAG, "Dictionary chosen: " + currentDictionaryID);
        setDictionaryCheckedOnCreate(currentDictionaryID);

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            // Hide Difficulty option for all other dictionaries than DTU
            setModeButtonVisibility();
            setDictionary();
        });

        switchToHardDTU.setOnClickListener((View v) -> {
            if (switchToHardDTU.isChecked()) {
                hardModeSet = true;
                Log.d(TAG, "HARD mode selected.");
            } else {
                hardModeSet = false;
                Log.d(TAG, "HARD mode deselected.");
            }

            setDictionary();
        });

        backbutton.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    private void setDictionaryCheckedOnCreate(int id) {
        // Sets the right dictionary as checked on create.
        switch (id) {
            case 0:
                defaultButton.setChecked(true);
                break;
            case 1:
                dtuButton.setChecked(true);
                break;
            case 2:
                drButton.setChecked(true);
                break;
        }
        setModeButtonVisibility();
    }

    private void setModeButtonVisibility() {
        if (!dtuButton.isChecked()) {
            switchToHardDTU.setVisibility(View.INVISIBLE);
        } else {
            switchToHardDTU.setVisibility(View.VISIBLE);
        }
    }

    private void setDictionary() {

        if (defaultButton.isChecked()) {
            dictionaryId = 0;
        } else if (dtuButton.isChecked()) {
            dictionaryId = 1;
        } else if (drButton.isChecked()) {
            dictionaryId = 2;
        }

        new DataDownloader().execute();
    }

    private class DataDownloader extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            loading.setVisibility(View.VISIBLE);
            loading.setText(R.string.henter_ordliste);
            backbutton.setEnabled(false);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Log.d(TAG, "Setting dictionary: ");
            if (dictionaryId == 0) {
                game.setDictionary(0, "");
            } else if (hardModeSet) {
                game.setDictionary(1, "3");
            } else if (dictionaryId == 1) {
                game.setDictionary(1, "12");
            } else if (dictionaryId == 2) {
                game.setDictionary(2, "");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            loading.setText(R.string.donedownloading);
            backbutton.setEnabled(true);
        }
    }
}
