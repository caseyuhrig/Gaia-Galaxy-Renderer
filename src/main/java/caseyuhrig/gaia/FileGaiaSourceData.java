package caseyuhrig.gaia;

import caseyuhrig.io.ProgressInputStream;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

public class FileGaiaSourceData implements Iterable<FileRenderingSource>, AutoCloseable {
    private SourceDataIterator iterator;


    @Override
    public @NotNull Iterator<FileRenderingSource> iterator() {
        iterator = new SourceDataIterator();
        return iterator;
    }

    public void stop() {
        if (iterator != null) {
            iterator.cancel();
        }
    }

    @Override
    public void forEach(final Consumer<? super FileRenderingSource> action) {
        throw new UnsupportedOperationException("forEach Not implemented");
    }

    @Override
    public Spliterator<FileRenderingSource> spliterator() {
        throw new UnsupportedOperationException("spliterator Not implemented");
    }

    public double getProgress() {
        return iterator.percent;
    }

    @Override
    public void close() throws Exception {
        if (iterator != null) {
            iterator.close();
        }
    }

    private static class SourceDataIterator implements Iterator<FileRenderingSource>, AutoCloseable {

        private final DataInputStream input;
        private boolean done = false;
        public double percent = 0.0;

        public SourceDataIterator() {
            try {
                final var file = new File("d:/gaia.dat");
                input = new DataInputStream(new BufferedInputStream(
                        new ProgressInputStream(new FileInputStream(file), file.length(), progress -> {
                            //System.out.println("Progress: " + progress);
                            percent = progress;
                        })));

            } catch (final Throwable throwable) {
                throw new RuntimeException(throwable.getLocalizedMessage(), throwable);
            }
        }

        public void cancel() {
            done = true;
        }

        @Override
        public boolean hasNext() {
            try {
                return !done && input.available() > 0;
            } catch (final Throwable e) {
                throw new RuntimeException(e.getLocalizedMessage(), e);
            }
        }

        @Override
        public FileRenderingSource next() {
            //n++;
            return new FileRenderingSource(input);
        }

        @Override
        public void close() throws Exception {
            try {
                input.close();
            } catch (final Throwable e) {
                System.err.println(e.getLocalizedMessage());
                e.printStackTrace(System.err);
            }
        }
    }
}
