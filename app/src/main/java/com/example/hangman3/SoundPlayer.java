package com.example.hangman3;

import android.content.Context;
import android.media.MediaPlayer;

import com.example.hangman3.model.ThreadPerTaskExecutor;

import java.util.concurrent.Executor;

public class SoundPlayer {
    private Executor executor = new ThreadPerTaskExecutor();
    private Context context;

    public SoundPlayer(Context context) {
        this.context = context;
    }

    public void play(String soundName) {
        int id;

        switch (soundName) {
            case "rope":
                id = R.raw.rope;
                break;
            case "crack":
                id = R.raw.crack;
                break;
            default:
                id = R.raw.birds;
        }

        executor.execute(() -> {
            MediaPlayer mediaPlayer = MediaPlayer.create(context, id);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    mp.reset();
                    mp.release();
                }
            });
        });
    }
}
