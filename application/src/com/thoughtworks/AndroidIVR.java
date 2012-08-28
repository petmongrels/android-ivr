package com.thoughtworks;

import android.app.Activity;
import android.os.Bundle;
import com.thoughtworks.androidivr.audiorecorder.AudioRecorder;

import java.io.IOException;

public class AndroidIVR extends Activity {
    private AudioRecorder audioRecorder;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        audioRecorder = new AudioRecorder();
        try {
            audioRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        audioRecorder.stop();
        super.onPause();
    }
}
