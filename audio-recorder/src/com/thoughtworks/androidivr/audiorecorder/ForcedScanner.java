package com.thoughtworks.androidivr.audiorecorder;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.util.Log;

public class ForcedScanner implements MediaScannerConnection.MediaScannerConnectionClient {
    private MediaScannerConnection mConnection;
    private String path;
    private static final String LOG_TAG = ForcedScanner.class.getName();

    // filePath - where to scan;
    public ForcedScanner(Context ctx, String filePath) {
        path = filePath;
        mConnection = new MediaScannerConnection(ctx, this);
    }

    public void scan() {
        mConnection.connect();
    }

    // start the scan when scanner is ready
    // mime type of media to scan i.e. "image/jpeg".
    // use "*/*" for any media
    public void onMediaScannerConnected() {
        mConnection.scanFile(path, "*/*");
        Log.i(LOG_TAG, String.format("media file scanned: %s", path));
    }

    @Override
    public void onScanCompleted(String path, Uri uri) {
        Log.i(LOG_TAG, String.format("media file scanned: %s", this.path));
    }
}