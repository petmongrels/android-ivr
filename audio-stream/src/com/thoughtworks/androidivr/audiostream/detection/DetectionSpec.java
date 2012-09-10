package com.thoughtworks.androidivr.audiostream.detection;

/**
 * FrameSize = number of data points for a signal captured
 * frameSize should be 160. samples = 102
 */
public class DetectionSpec {
    private short frameSize;
    private short maxButtonExpected;

    public short frameSize() {
        return frameSize;
    }

    public short maxButtonExpected() {
        return maxButtonExpected;
    }
}