package com.thoughtworks.androidivr.audiostream.wav;

import android.util.Log;
import com.thoughtworks.androidivr.audiostream.AudioStreamListener;
import com.thoughtworks.androidivr.audiostream.sampling.SamplingSpec;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class WAVEFileWriter implements AudioStreamListener {
    private RandomAccessFile fileWriter;
    private File file;
    private static final String LOG_TAG = WAVEFileWriter.class.getName();
    private int totalNumberOfBytes;

    public WAVEFileWriter(File file, SamplingSpec samplingSpec) throws IOException {
        this.file = file;
        fileWriter = new RandomAccessFile(file, "rw");

        fileWriter.setLength(0); // Set file length to 0, to prevent unexpected behavior in case the file already existed
        fileWriter.writeBytes("RIFF");
        fileWriter.writeInt(0); // Final file size not known yet, write 0
        fileWriter.writeBytes("WAVE");
        fileWriter.writeBytes("fmt ");
        fileWriter.writeInt(Integer.reverseBytes(16)); // Sub-chunk size, 16 for PCM
        fileWriter.writeShort(Short.reverseBytes((short) 1)); // AudioFormat, 1 for PCM
        fileWriter.writeShort(Short.reverseBytes(samplingSpec.numberOfChannels()));// Number of channels, 1 for mono, 2 for stereo
        fileWriter.writeInt(Integer.reverseBytes(samplingSpec.sampleRate())); // Sample rate
        fileWriter.writeInt(Integer.reverseBytes(samplingSpec.sampleRate() * samplingSpec.sampleSizeInBytes() * samplingSpec.numberOfChannels())); // Byte rate, SampleRate*NumberOfChannels*BitsPerSample/8
        fileWriter.writeShort(Short.reverseBytes((short) (samplingSpec.numberOfChannels() * samplingSpec.sampleSizeInBytes()))); // Block align, NumberOfChannels*BitsPerSample/8
        fileWriter.writeShort(Short.reverseBytes(samplingSpec.sampleSizeInBits())); // Bits per sample
        fileWriter.writeBytes("data");
        fileWriter.writeInt(0); // Data chunk size not known yet, write 0
    }

    public void write(byte[] buffer) {
        try {
            fileWriter.write(buffer);
            totalNumberOfBytes += buffer.length;
            Log.i(LOG_TAG, String.format("Total number of bytes written=%d", totalNumberOfBytes));
        } catch (IOException e) {
            Log.e(LOG_TAG, "", e);
            throw new RuntimeException(e);
        }
    }

    public void close() {
        try {
            fileWriter.seek(4); // Write size to RIFF header
            fileWriter.writeInt(Integer.reverseBytes(36 + totalNumberOfBytes));
            fileWriter.seek(40); // Write size to Subchunk2Size field
            fileWriter.writeInt(Integer.reverseBytes(totalNumberOfBytes));
            fileWriter.close();

            Log.i(LOG_TAG, String.format("File size=%d", file.length()));
        } catch (IOException e) {
            Log.e(LOG_TAG, "", e);
            throw new RuntimeException(e);
        }
    }
}