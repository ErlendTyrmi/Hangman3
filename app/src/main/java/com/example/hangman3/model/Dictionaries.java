package com.example.hangman3.model;

import java.util.ArrayList;
import java.util.HashMap;

public class Dictionaries {
    // Words are stored here but not serialized.

    private static Dictionaries dm;
    private HashMap<Integer, ArrayList<String>> dictionaries = new HashMap<>();
    private int id = 0; // default 0.

    public static Dictionaries getDictionaryManager() {
        if (dm == null) {
            dm = new Dictionaries();
        }
        return dm;
    }

    public HashMap<Integer, ArrayList<String>> getDictionaries() {
        return dictionaries;
    }

    public void addDictionary(int index, ArrayList<String> newDictionary) {
        dictionaries.put(index, newDictionary);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<String> getDictionary() {
        return dictionaries.get(id);
    }
}
