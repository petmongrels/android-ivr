package com.thoughtworks.androidivr.audiorecorder;

import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;
import com.thoughtworks.androidivr.audiostream.sampling.SamplingSpec;
import com.thoughtworks.androidivr.audiostream.wav.WAVEFileWriter;

import java.io.File;
import java.io.IOException;

public class AudioRecorderImpl implements Runnable {
    private static final String LOG_TAG = AudioRecorderImpl.class.getName();
    private AudioRecord androidRecorder;
    private WAVEFileWriter waveFileWriter;
    private SamplingSpec samplingSpec;

    public AudioRecorderImpl(SamplingSpec samplingSpec, File file) throws IOException {
        this.samplingSpec = samplingSpec;
        waveFileWriter = new WAVEFileWriter(file, samplingSpec);
    }

    private void record() throws IOException {
        Log.i(LOG_TAG, "Start recording");
        androidRecorder = new AudioRecord(MediaRecorder.AudioSource.VOICE_DOWNLINK, samplingSpec.sampleRate(), samplingSpec.channelIn(), samplingSpec.audioEncoding(), samplingSpec.bufferSize());
        if (androidRecorder.getState() != AudioRecord.STATE_INITIALIZED) {
            Log.e(LOG_TAG, "AudioRecord initialization failed");
            throw new RuntimeException("AudioRecord initialization failed");
        }
        androidRecorder.startRecording();
        byte[] buffer = new byte[samplingSpec.bufferSize()];
        while (androidRecorder.getRecordingState() != AudioRecord.RECORDSTATE_STOPPED) {
            Log.i(LOG_TAG, "bytes read: " + androidRecorder.read(buffer, 0, buffer.length));
            waveFileWriter.write(buffer);
        }
    }

    public void stop() {
        androidRecorder.stop();
        androidRecorder.release();
    }

    public void saveToFile() {
        waveFileWriter.close();
        Log.i(LOG_TAG, "Stopping audio recorder");
    }

    @Override
    public void run() {
        try {
            record();
        } catch (Exception e) {
            Log.e(LOG_TAG, "", e);
        }
    }
}