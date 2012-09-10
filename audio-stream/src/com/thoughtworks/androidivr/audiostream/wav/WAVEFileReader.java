package com.thoughtworks.androidivr.audiostream.wav;

import com.thoughtworks.androidivr.audiostream.detection.SampleProvider;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class WAVEFileReader implements SampleProvider {
    private RandomAccessFile fileReader;
    private int byteCount;
    private byte[] buffer;

    public WAVEFileReader(File file, int byteCount) throws IOException {
        this.byteCount = byteCount;
        fileReader = new RandomAccessFile(file, "r");
        fileReader.seek(file.length());
        buffer = new byte[byteCount];
    }

    @Override
    public short[] read() {
//        fileReader.readFully();
        return new short[0];
    }
}