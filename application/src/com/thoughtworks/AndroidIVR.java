package com.thoughtworks;

import android.app.Activity;
import android.media.AudioFormat;
import android.os.Bundle;
import android.util.Log;
import com.thoughtworks.androidivr.audiorecorder.AudioStreamProcessor;
import com.thoughtworks.androidivr.audiostream.sampling.SamplingSpec;

import java.io.IOException;

public class AndroidIVR extends Activity {
    private AudioStreamProcessor audioStreamProcessor;
//    44100
    private static final SamplingSpec samplingSpec = new SamplingSpec(AudioFormat.ENCODING_PCM_16BIT, AudioFormat.CHANNEL_IN_MONO, 8000);
    private static final String LOG_TAG = AndroidIVR.class.getName();

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    private void start() throws IOException {
        Log.i(LOG_TAG, "Starting audio recorder");
        audioStreamProcessor = new AudioStreamProcessor(samplingSpec, null);
        audioStreamProcessor.start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "", e);
        }
    }

    @Override
    protected void onPause() {
        stopRecording();
        super.onPause();
    }

    private void stopRecording() {
        if (audioStreamProcessor.isStopped()) return;
        audioStreamProcessor.stop();
    }

    @Override
    protected void onStop() {
        stopRecording();
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        stopRecording();
        super.onBackPressed();
    }
}