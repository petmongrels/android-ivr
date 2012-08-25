package com.thoughtworks.androidivr.audiorecorder;

import android.app.Activity;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.IOException;

public class AudioRecorder extends Activity {
    private static final String LOG_TAG = "AudioRecorder";
    private static String mFileName = null;
    private MediaRecorder mRecorder = null;

    public AudioRecorder() {
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/audiorecordtest.3gp";
    }

    public void start() throws IOException {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_DOWNLINK);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecorder.prepare();
    }

    public void stop() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

    public void cleanup() {
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }
    }
}