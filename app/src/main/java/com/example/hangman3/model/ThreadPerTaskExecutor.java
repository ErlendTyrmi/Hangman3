package com.example.hangman3.model;

import java.util.concurrent.Executor;

public class ThreadPerTaskExecutor implements Executor {
    public void execute(Runnable r) {
        new Thread(r).start();
    }
}

