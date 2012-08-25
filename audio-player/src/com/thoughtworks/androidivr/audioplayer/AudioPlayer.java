package com.thoughtworks.androidivr.audioplayer;

import android.media.MediaPlayer;

import java.io.IOException;

public class AudioPlayer {
    private MediaPlayer mPlayer = null;

    public void startPlaying(String fileName) throws IOException {
        mPlayer = new MediaPlayer();
        mPlayer.setDataSource(fileName);
        mPlayer.prepare();
        mPlayer.start();
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }
}