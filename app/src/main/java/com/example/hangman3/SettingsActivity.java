package com.example.hangman3;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.example.hangman3.model.Game;

import java.util.ArrayList;
import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity {
    private final String TAG = "SettingsActivity";
    private RadioGroup radioGroup;
    private RadioButton defaultButton, dtuButton, drButton;
    private Switch switchToHardDTU;
    private TextView loading;
    private ImageButton backbutton;
    private ProgressBar progressBar2;
    private Game game;
    private int dictionaryId;
    HashMap<Integer, ArrayList<String>> dictionaries;
    private boolean hardModeSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        radioGroup = findViewById(R.id.radioGroup);
        defaultButton = findViewById(R.id.radioButtonDefault);
        dtuButton = findViewById(R.id.radioButtonDTU);
        drButton = findViewById(R.id.radioButtonDR);
        switchToHardDTU = findViewById(R.id.switchToHardDTU);
        loading = findViewById(R.id.dictionaryLoadingTextView);
        backbutton = findViewById(R.id.backButton);
        progressBar2 = findViewById(R.id.progressBar2);
        game = ViewModelProviders.of(this).get(Game.class);
        int currentDictionaryID = game.getCurrentDictionaryID();
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
            finish();
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
                dtuButton.setChecked(true); // Two DTU lists
                break;
            case 3:
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

    private class DataDownloader extends AsyncTask<Void, Boolean, Boolean> {

        @Override
        protected void onPreExecute() {
            loading.setVisibility(View.VISIBLE);
            loading.setText(R.string.henter_ordliste);
            progressBar2.setVisibility(View.VISIBLE);
            backbutton.setEnabled(false);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            // Retry download if dictionary missing.
            dictionaries = game.getDictionaries();
            boolean allImported = true;
            for (int i = 0; i < 4; i++) {
                if (dictionaries.get(i) == null) {
                    allImported = false;
                } else if (dictionaries.get(i).size() < 1) {
                    allImported = false;
                }
            }
            if (!allImported) {
                try {
                    game.importDictionaries();
                } catch (Exception e) {
                    return false;
                }
            }

            Log.d(TAG, "Setting dictionary: ");
            try {
                if (dictionaryId == 0) {
                    game.setDictionary(0);
                } else if (hardModeSet) {
                    game.setDictionary(2);
                } else if (dictionaryId == 1) {
                    game.setDictionary(1);
                } else if (dictionaryId == 2) {
                    // 2 -> 3 because 1 has two difficulty settings
                    game.setDictionary(3);
                }
            } catch (Exception e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean works) {
            progressBar2.setVisibility(View.INVISIBLE);
            if (!works) {
                Toast.makeText(SettingsActivity.this, "Ingen netforbindelse.", Toast.LENGTH_SHORT).show();
                loading.setText("Download fejlede.");
            } else {
                loading.setText(R.string.donedownloading);
            }
            backbutton.setEnabled(true);
        }
    }
}
