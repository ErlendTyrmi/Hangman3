package com.example.hangman3;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hangman3.logic.Game;
import com.example.hangman3.logic.GameInterface;

public class SettingsActivity extends AppCompatActivity {
    RadioGroup radioGroup;
    RadioButton defaultButton, dtuButton, drButton;
    Switch switchToHardDTU;
    GameInterface game = Game.getGame();
    ThreadPerTaskExecutor executor = new ThreadPerTaskExecutor();

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

        int currentDictionaryID = intent.getIntExtra("currentDictionaryID", 0);
        System.out.println(currentDictionaryID);
        setDictionaryChecked(currentDictionaryID);

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            // checkedId is the RadioButton selected

            Toast.makeText(this, "Ja ja", Toast.LENGTH_SHORT).show();
            // Hide Difficulty option for all other dictionaries than DTU
            if (!dtuButton.isChecked()) {
                switchToHardDTU.setVisibility(View.INVISIBLE);
            } else {
                switchToHardDTU.setVisibility(View.VISIBLE);
            }
            setDictionary();
        });

        switchToHardDTU.setOnClickListener((View v) -> {
            Log.d("Settings", "HARD mode set.");
            // TODO: ERROR HANDLING
            setDictionary();
        });
    }

    private void setDictionaryChecked(int id) {

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
    }

    private void setDictionary() {
        executor.execute(() -> {

            Log.d("Settings", "setDictionary alled.");
            if (defaultButton.isChecked()) {
                game.setDictionary(0, "");
            } else if (dtuButton.isChecked() && !switchToHardDTU.isActivated()) {
                game.setDictionary(1, "1");
            } else if (switchToHardDTU.isActivated()) {
                game.setDictionary(1, "23");
            } else if (drButton.isChecked()) {
                game.setDictionary(2, "");
            }

        });

    }
}
