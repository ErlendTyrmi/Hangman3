package com.example.hangman3.logic;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

import static android.content.Context.MODE_PRIVATE;

public class GameData {
    private final String dataFileName = "gameData.txt";
    private Context context;

    public GameData(Context context) {
        this.context = context;
    }

    public void storeGameData(int[] data) {

        String input = Arrays
                .stream(data)
                .mapToObj(String::valueOf)
                .reduce((a, b) -> a.concat(",").concat(b))
                .get();

        Log.d("Storing data", "Data from game, converted to string: " + input);

        // New thread
        try (FileOutputStream outputStream = context.openFileOutput(
                dataFileName, MODE_PRIVATE)) {
            outputStream.write(input.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int[] readGameData() {
        int[] data = new int[3];
        try (FileInputStream fileInputStream = context.openFileInput(dataFileName)) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            String dataString = bufferedReader.readLine();
            String[] dataStringArray = dataString.split(",");
            data[0] = Integer.parseInt(dataStringArray[0]);
            data[1] = Integer.parseInt(dataStringArray[1]);
            data[2] = Integer.parseInt(dataStringArray[2]);
            Log.d("ReadGameData", "Imported data from file: " + data[0] + ", " + data[1] + " and " + data[2]);
        } catch (FileNotFoundException f) {
            Log.e("readGameData", "Could not find a data file.");
            f.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            // Sleep to return updated data
            Thread.sleep(1000);
            Log.d("ReadGameData", "Returning data from file: " + data[0] + ", " + data[1] + " and " + data[2]);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return data;
    }
}
