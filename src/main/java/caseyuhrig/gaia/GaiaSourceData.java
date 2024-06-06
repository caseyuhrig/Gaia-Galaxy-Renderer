package caseyuhrig.gaia;

import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

public class GaiaSourceData implements Iterable<GaiaRenderingSource>, AutoCloseable {
    private SourceDataIterator iterator;

    @Override
    public @NotNull Iterator<GaiaRenderingSource> iterator() {
        iterator = new SourceDataIterator();
        return iterator;
    }

    public void stop() {
        if (iterator != null) {
            iterator.cancel();
        }
    }

    @Override
    public void forEach(final Consumer<? super GaiaRenderingSource> action) {
        throw new UnsupportedOperationException("forEach Not implemented");
    }

    @Override
    public Spliterator<GaiaRenderingSource> spliterator() {
        throw new UnsupportedOperationException("spliterator Not implemented");
    }

    @Override
    public void close() throws Exception {
        if (iterator != null) {
            iterator.close();
        }
    }

    private static class SourceDataIterator implements Iterator<GaiaRenderingSource>, AutoCloseable {

        private final static String SELECT_SQL = "SELECT " + GaiaRenderingSource.getFieldNames() + " FROM source";
        private final Connection connection;
        private final PreparedStatement statement;
        private final ResultSet rs;
        private Long n = -1L;
        private Boolean done = false;

        public SourceDataIterator() {
            try {
                final var url = "jdbc:postgresql://localhost:5432/gaia";
                final var user = "gaia";
                final var password = "gaia";
                connection = DriverManager.getConnection(url, user, password);
                connection.setAutoCommit(false);
                n = -1L;
                statement = connection.prepareStatement(SELECT_SQL);
                statement.setFetchSize(150);
                rs = statement.executeQuery();
            } catch (final SQLException e) {
                throw new RuntimeException(e.getLocalizedMessage(), e);
            }
        }

        public void cancel() {
            done = true;
        }

        @Override
        public boolean hasNext() {
            try {
                return !done && rs.next();
            } catch (final SQLException e) {
                throw new RuntimeException(e.getLocalizedMessage(), e);
            }
        }

        @Override
        public GaiaRenderingSource next() {
            n++;
            return new GaiaRenderingSource(rs);
        }

        @Override
        public void close() throws Exception {
            try {
                rs.close();
            } catch (final SQLException e) {
                System.err.println(e.getLocalizedMessage());
            }
            try {
                statement.close();
            } catch (final SQLException e) {
                System.err.println(e.getLocalizedMessage());
            }
            try {
                connection.close();
            } catch (final SQLException e) {
                System.err.println(e.getLocalizedMessage());
            }
        }
    }
}
