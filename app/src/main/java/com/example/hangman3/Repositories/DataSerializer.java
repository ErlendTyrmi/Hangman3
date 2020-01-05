package com.example.hangman3.Repositories;

import android.content.Context;
import android.util.Log;

import com.example.hangman3.model.GameDataObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class DataSerializer {
    //private final String DataFileName = "GameData.txt";
    private final String fileName = "HangmanData.txt";
    private Context context;
    private final String TAG = "DataSerializer";

    public DataSerializer(Context context) {
        this.context = context;
    }

    // Handle storage of high scores

    public void StoreGameData(GameDataObject gameData) throws IOException {
        FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
        ObjectOutputStream os = new ObjectOutputStream(fos);
        Log.d(TAG, "StoreGameData: Saving " + gameData.toString());
        os.writeObject(gameData);
        os.close();
        fos.close();
    }

    public GameDataObject getGameData() throws IOException, ClassNotFoundException {
        FileInputStream fileInputStream = context.openFileInput(fileName);
        ObjectInputStream inputStream = new ObjectInputStream(fileInputStream);
        GameDataObject gameData = (GameDataObject) inputStream.readObject();
        inputStream.close();
        fileInputStream.close();
        Log.d(TAG, "getGameData: Opened " + gameData.toString());
        return gameData;
    }
}
