package com.thoughtworks.androidivr.audiorecorder;

import android.app.Activity;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.IOException;

public class AudioRecorder extends Activity {
    private static final String LOG_TAG = "AudioRecorder";
    private String fileName;
    private MediaRecorder mediaRecorder = null;

    public AudioRecorder() {
        fileName = String.format("%s/temp-audio-file.mp3", Environment.getExternalStorageDirectory().getAbsolutePath());
        Log.i(LOG_TAG, "Writing to file: " + fileName);
    }

    public void start() throws IOException {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        mediaRecorder.setOutputFile(fileName);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.prepare();
        mediaRecorder.start();
    }

    public void stop() {
        Log.i(LOG_TAG, "Stopping audio recorder");
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
    }
}