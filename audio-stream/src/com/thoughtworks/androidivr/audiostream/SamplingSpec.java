package com.thoughtworks.androidivr.audiostream;

import android.media.AudioFormat;
import android.media.AudioRecord;

/**
 * Frame = number of samples per second
 * Number of channel = Mono=1, Stereo=2, so on
 * Pulse-code modulation (PCM) is a method used to digitally represent sampled analog signals.
 * Sample rate = number of data points per second taken from the analog wave signal
 * Sample size = number of bits per sample
 */
public class SamplingSpec {
    private int sampleRate;
    private int audioEncoding;
    private int channelIn;

    public SamplingSpec(int audioEncoding, int channelIn, int sampleRate) {
        this.audioEncoding = audioEncoding;
        this.channelIn = channelIn;
        this.sampleRate = sampleRate;
    }

    public short numberOfChannels() {
        return (short) (AudioFormat.ENCODING_PCM_16BIT == audioEncoding ? 2 : 1);
    }

    public short sampleSizeInBits() {
        return (short) (AudioFormat.ENCODING_PCM_16BIT == audioEncoding ? 16 : 8);
    }

    public short sampleSizeInBytes() {
        return (short) (sampleSizeInBits() / 8);
    }

    public int sampleRate() {
        return this.sampleRate;
    }

    public int channelIn() {
        return channelIn;
    }

    public int audioEncoding() {
        return audioEncoding;
    }

    public int bufferSize() {
        return AudioRecord.getMinBufferSize(sampleRate, channelIn, audioEncoding);
    }

    public int framePeriod() {
        return bufferSize() / ( sampleSizeInBits() * sampleRate * numberOfChannels() / 8 );
    }
}