package com.example.hangman3.logic;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.concurrent.Executor;

import static android.content.Context.MODE_PRIVATE;

public class GameDataWriter {
    private final String dataFileName = "gameData.txt";
    private GameInterface game;
    private Executor executor = new ThreadPerTaskExecutor();
    private Context context;

    public GameDataWriter(GameInterface game, Context context) {
        this.game = game;
        this.context = context;
    }


    public void importGameData() {
        int[] gameData = readGameData();
        game.setStreakCount(gameData[0]);
        game.setHighScore(gameData[1]);
    }

    public void storeGameData(int[] data) {

        String input = Arrays
                .stream(data)
                .mapToObj(String::valueOf)
                .reduce((a, b) -> a.concat(",").concat(b))
                .get();

        Log.d("Storing data", "Data from game, converted to string: " + input);

        // New thread
        executor.execute(() -> {
            try (FileOutputStream outputStream = context.openFileOutput(
                    dataFileName, MODE_PRIVATE)) {
                outputStream.write(input.getBytes());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public int[] readGameData() {
        int[] data = new int[2];
        // New thread TODO: Maybe set variables from here? The array does not change.
        executor.execute(() -> {
            try (FileInputStream fileInputStream = context.openFileInput(dataFileName)) {
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
}
