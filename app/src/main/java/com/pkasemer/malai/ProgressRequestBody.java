package com.pkasemer.malai;

import android.os.Handler;
import android.os.Looper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

public class ProgressRequestBody extends RequestBody {
    private File mFile;
    private String mPath;
    private UploadCallbacks mListener;

    private static final int DEFAULT_BUFFER_SIZE = 2048;

    public interface UploadCallbacks {
        void onProgressUpdate(int percentage);
        void onError();
        void onFinish();
    }

    public ProgressRequestBody(final File file,  final  UploadCallbacks listener) {
//        this.content_type = requestBody.contentType();
        mFile = file;
        mListener = listener;
    }



    @Override
    public MediaType contentType() {
        return MediaType.parse("image/*");
    }

    @Override
    public long contentLength() throws IOException {
        return mFile.length();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        long fileLength = mFile.length();
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        FileInputStream in = new FileInputStream(mFile);
        long uploaded = 0;

        try {
            int read;
            Handler handler = new Handler(Looper.getMainLooper());
            int num = 0;
            while ((read = in.read(buffer)) != -1) {

                int progress = (int) (100 * uploaded / fileLength);
                if( progress > num + 1 ){
                    // update progress on UI thread
                    handler.post(new ProgressUpdater(uploaded, fileLength));
                    num = progress;
                }

                uploaded += read;
                sink.write(buffer, 0, read);
            }
        } finally {
            in.close();
        }
    }

    private class ProgressUpdater implements Runnable {
        private long mUploaded;
        private long mTotal;
        public ProgressUpdater(long uploaded, long total) {
            mUploaded = uploaded;
            mTotal = total;
        }

        @Override
        public void run() {
            mListener.onProgressUpdate((int)(100 * mUploaded / mTotal));
        }
    }
}