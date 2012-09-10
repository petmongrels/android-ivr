package com.thoughtworks.androidivr.audiorecorder;

import android.util.Log;

import java.io.File;

public class AudioFile {
    private static final String LOG_TAG = AudioFile.class.getName();
    private File directory;

    public AudioFile(File directory) {
        this.directory = directory;
        directory.mkdir();
    }

    public File nextFile() {
        File[] list = directory.listFiles();
        Log.i(LOG_TAG, String.format("Found: %d files", list.length));
        for (File file : list) {
            Log.i(LOG_TAG, String.format("Deleting file: %s", file.getAbsolutePath()));
//            boolean deleted = file.delete();
//            Log.i(LOG_TAG, String.format("File %s delete status: %b", file.getAbsolutePath(), deleted));
        }

        String fileName = String.format("%s/temp-audio-file.wav", directory.getAbsolutePath());
        Log.i(LOG_TAG, String.format("Writing to file: %s", fileName));
        File file = new File(fileName);
        file.delete();
        Log.i(LOG_TAG, String.format("Deleting file: %s", file.getAbsolutePath()));
        return file;
    }
}