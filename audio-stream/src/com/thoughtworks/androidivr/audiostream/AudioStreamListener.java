package com.thoughtworks.androidivr.audiostream;

public interface AudioStreamListener {
    void write(byte[] buffer);
    void close();
}
