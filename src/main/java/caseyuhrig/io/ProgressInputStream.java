package caseyuhrig.io;

import java.io.IOException;
import java.io.InputStream;

public class ProgressInputStream extends InputStream {
    private final InputStream inputStream;
    private long bytesRead = 0;
    private final long totalBytes;
    private final ProgressListener progressListener;
    private double progress = 0.0;


    public ProgressInputStream(final InputStream inputStream, final long totalBytes, final ProgressListener progressListener) {
        this.inputStream = inputStream;
        this.totalBytes = totalBytes;
        this.progressListener = progressListener;
    }

    @Override
    public int read() throws IOException {
        final int byteRead = inputStream.read();
        if (byteRead != -1) {
            bytesRead++;
            updateProgress();
        }
        return byteRead;
    }

    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        final int bytesReadNow = inputStream.read(b, off, len);
        if (bytesReadNow != -1) {
            bytesRead += bytesReadNow;
            updateProgress();
        }
        return bytesReadNow;
    }

    public double getProgress() {
        return progress;
    }

    private void updateProgress() {
        if (progressListener != null && totalBytes > 0) {
            progress = (double) bytesRead / (double) totalBytes;
            progressListener.onProgressUpdate(progress);
        }
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
        super.close();
    }

    @Override
    public int available() throws IOException {
        return inputStream.available();
    }

    @Override
    public synchronized void mark(final int readlimit) {
        inputStream.mark(readlimit);
    }

    @Override
    public synchronized void reset() throws IOException {
        inputStream.reset();
    }

    @Override
    public boolean markSupported() {
        return inputStream.markSupported();
    }

    public interface ProgressListener {
        void onProgressUpdate(double percentage);
    }
}
