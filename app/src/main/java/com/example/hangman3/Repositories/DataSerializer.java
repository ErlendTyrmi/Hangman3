package com.example.hangman3.Repositories;

import android.content.Context;

import com.example.hangman3.model.GameDataObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class DataSerializer {
    //private final String DataFileName = "GameData.txt";
    private final String HiScoreDataFileName = "HangmanData.txt";
    private Context context;

    public DataSerializer(Context context) {
        this.context = context;
    }

    /*
    // Handle ingame data (current score and streak)
    public void storeCurrentGameData(int[] data) {

        String input = Arrays
                .stream(data)
                .mapToObj(String::valueOf)
                .reduce((a, b) -> a.concat(",").concat(b))
                .get();

        Log.d("Storing data", "Data from game, converted to string: " + input);

        // New thread
        try (FileOutputStream outputStream = context.openFileOutput(
                DataFileName, MODE_PRIVATE)) {
            outputStream.write(input.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int[] readCurrentGameData() {
        int[] data = new int[3];
        try (FileInputStream fileInputStream = context.openFileInput(DataFileName)) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            String dataString = bufferedReader.readLine();
            String[] dataStringArray = dataString.split(",");
            data[0] = Integer.parseInt(dataStringArray[0]);
            data[1] = Integer.parseInt(dataStringArray[1]);
            data[2] = Integer.parseInt(dataStringArray[2]);
            Log.d("ReadCurrentGameData", "Imported data from file: " + data[0] + ", " + data[1] + " and " + data[2]);
        } catch (FileNotFoundException f) {
            Log.e("readCurrentGameData", "Could not find a data file.");
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
    */
    // Handle storage of high scores

    public void StoreGameData(GameDataObject gameData) throws IOException {
        FileOutputStream fos = context.openFileOutput(HiScoreDataFileName, Context.MODE_PRIVATE);
        ObjectOutputStream os = new ObjectOutputStream(fos);
        os.writeObject(gameData);
        os.close();
        fos.close();
    }

    public GameDataObject getGameData() throws IOException, ClassNotFoundException {
        FileInputStream fis = context.openFileInput(HiScoreDataFileName);
        ObjectInputStream is = new ObjectInputStream(fis);
        GameDataObject gameData = (GameDataObject) is.readObject();
        is.close();
        fis.close();
        return gameData;
    }
}
