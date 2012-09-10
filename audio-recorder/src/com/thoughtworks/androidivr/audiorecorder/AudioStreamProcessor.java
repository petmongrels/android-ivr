package com.thoughtworks.androidivr.audiorecorder;

import android.os.Environment;
import android.util.Log;
import com.thoughtworks.androidivr.audiostream.detection.DTMFDetector;
import com.thoughtworks.androidivr.audiostream.detection.DetectionSpec;
import com.thoughtworks.androidivr.audiostream.sampling.SamplingSpec;
import com.thoughtworks.androidivr.audiostream.wav.WAVEFileReader;

import java.io.File;
import java.io.IOException;

public class AudioStreamProcessor {
    private final AudioRecorderImpl audioRecorder;
    private Thread recordingThread;
    private Thread dtmfDetectionThread;
    private static final String LOG_TAG = AudioStreamProcessor.class.getName();
    private boolean isStopped;
    private DTMFDetector dtmfDetector;

    public AudioStreamProcessor(SamplingSpec samplingSpec, DetectionSpec detectionSpec) throws IOException {
        File outputDir = new File(Environment.getExternalStorageDirectory(), "test");
        AudioFile audioFile = new AudioFile(outputDir);
        File file = audioFile.nextFile();
        audioRecorder = new AudioRecorderImpl(samplingSpec, file);
//        short byteCount = samplingSpec.frameSizeInBytes(detectionSpec.frameSize());
//        dtmfDetector = new DTMFDetector(detectionSpec, new WAVEFileReader(file, byteCount));
    }

    public void start() {
        recordingThread = new Thread(audioRecorder);
        recordingThread.start();
//
//        dtmfDetectionThread = new Thread(dtmfDetector);
//        dtmfDetectionThread.start();
    }

    public void stop() {
        isStopped = true;
        audioRecorder.stop();
        while (recordingThread.isAlive()) {
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