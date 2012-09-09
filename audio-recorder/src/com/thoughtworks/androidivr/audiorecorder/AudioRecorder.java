package com.thoughtworks.androidivr.audiorecorder;

import android.util.Log;
import com.thoughtworks.androidivr.audiostream.SamplingSpec;

import java.io.IOException;

public class AudioRecorder {
    private final AudioRecorderImpl audioRecorder;
    private Thread thread;
    private static final String LOG_TAG = AudioRecorder.class.getName();
    private boolean isStopped;

    public AudioRecorder(SamplingSpec samplingSpec) throws IOException {
        audioRecorder = new AudioRecorderImpl(samplingSpec);
    }

    public void start() {
        thread = new Thread(audioRecorder);
        thread.start();
    }

    public void stop() {
        isStopped = true;
        audioRecorder.stop();
        while (thread.isAlive()) {
            try {
                Log.i(LOG_TAG, "Waiting for the recorder to stop");
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Log.e(LOG_TAG, "", e);
            }
        }
        audioRecorder.saveToFile();
    }

    public boolean isStopped() {
        return isStopped;
    }
}