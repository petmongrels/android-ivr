package com.thoughtworks.androidivr.audiorecorder;

import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;
import com.thoughtworks.androidivr.audiostream.SamplingSpec;
import com.thoughtworks.androidivr.audiostream.WAVEFile;

import java.io.File;
import java.io.IOException;

public class AudioRecorderImpl implements Runnable {
    private static final String LOG_TAG = AudioRecorderImpl.class.getName();
    private AudioRecord androidRecorder;
    public static File outputDir = new File(Environment.getExternalStorageDirectory(), "test");
    private WAVEFile waveFile;
    private SamplingSpec samplingSpec;

    public AudioRecorderImpl(SamplingSpec samplingSpec) throws IOException {
        this.samplingSpec = samplingSpec;
        outputDir.mkdir();
        File[] list = outputDir.listFiles();
        Log.i(LOG_TAG, String.format("Found: %d files", list.length));
        for (File file : list) {
            Log.i(LOG_TAG, String.format("Deleting file: %s", file.getAbsolutePath()));
            boolean deleted = file.delete();
            Log.i(LOG_TAG, String.format("File %s delete status: %b", file.getAbsolutePath(), deleted));
        }

        String fileName = String.format("%s/temp-audio-file.wav", outputDir.getAbsolutePath());
        Log.i(LOG_TAG, "Writing to file: " + fileName);
        waveFile = new WAVEFile(new File(fileName), samplingSpec);
    }

    private void record() throws IOException {
        Log.i(LOG_TAG, "Start recording");
        androidRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC, samplingSpec.sampleRate(), samplingSpec.channelIn(), samplingSpec.audioEncoding(), samplingSpec.bufferSize());
        if (androidRecorder.getState() != AudioRecord.STATE_INITIALIZED) {
            Log.e(LOG_TAG, "AudioRecord initialization failed");
            throw new RuntimeException("AudioRecord initialization failed");
        }
//        androidRecorder.setRecordPositionUpdateListener(waveFile);
//        androidRecorder.setPositionNotificationPeriod(samplingSpec.framePeriod());
        androidRecorder.startRecording();
        byte[] buffer = new byte[samplingSpec.bufferSize()];
        while (androidRecorder.getRecordingState() != AudioRecord.RECORDSTATE_STOPPED) {
            Log.i(LOG_TAG, "bytes read: " + androidRecorder.read(buffer, 0, buffer.length));
            waveFile.write(buffer);
        }
    }

    public void stop() {
        androidRecorder.stop();
        androidRecorder.release();
    }

    public void saveToFile() {
        try {
            waveFile.close();
            Log.i(LOG_TAG, "Stopping audio recorder");
        } catch (IOException e) {
            Log.e(LOG_TAG, "", e);
        }
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