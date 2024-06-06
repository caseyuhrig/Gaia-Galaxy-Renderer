package caseyuhrig.gaia;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

public class Main {


    public static void main(final String[] args) throws Exception {
        System.out.println("Hello, Gaia Java!");

        //renameFiles();

        //if (1 == 1) return;

        //loadFull();
        //loadSmall();
        update();

        System.out.println("Goodbye, Gaia Java!");
    }

    public static void loadSmall() {
        final AtomicInteger loadingCount = new AtomicInteger();
        loadingCount.set(0);

        final int maxFilesLoading = 10;


        final String folder = "F:\\Downloads\\gaia\\cdn.gea.esac.esa.int\\Gaia\\gdr3\\gaia_source";
        //final String fileName = "GaiaSource_000000-003111.csv.gz";
        //final String filePath = folder + "\\" + fileName;

        final var url = "jdbc:postgresql://localhost:5432/gaia_ssd";
        final var user = "gaia";
        final var password = "gaia";
        try (final var connection = DriverManager.getConnection(url, user, password)) {
            connection.setAutoCommit(false);

            final var files = Files.list(Paths.get(folder)).filter(p -> p.toString().endsWith(".csv.gz") || p.toString().endsWith(".csv.gz.done")).toList();

            for (final Path path : files) {

                System.out.println("Processing file: " + path);


                try (final var gzip = new GZIPInputStream(new FileInputStream(path.toFile()));
                     final var reader = new BufferedReader(new InputStreamReader(gzip))) {

                    int count = 0;
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (!line.startsWith("#")) { // skip comments
                            if (count > 0) { // skip header line
                                //final String line2 = line; // for use in lambda
                                try {
                                    final var fields = line.split(",");
                                    final var source_id = Convert.toLong(fields[2]);
                                    //if (!Gaia3DRSource.sourceExists(connection, source_id)) {
                                    try (final var preparedStatement = connection.prepareStatement("INSERT INTO source (source_id, ra, dec, parallax, bp_rp, bp_g, g_rp) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
                                        preparedStatement.setObject(1, source_id);
                                        preparedStatement.setObject(2, Convert.convert(fields[5], Double.class)); // ra
                                        preparedStatement.setObject(3, Convert.convert(fields[7], Double.class)); // dec
                                        preparedStatement.setObject(4, Convert.convert(fields[9], Double.class)); // parallax
                                        preparedStatement.setObject(5, Convert.convert(fields[96], Double.class)); // bp_rp
                                        preparedStatement.setObject(6, Convert.convert(fields[97], Double.class)); // bp_g
                                        preparedStatement.setObject(7, Convert.convert(fields[98], Double.class)); // g_rp
                                        preparedStatement.executeUpdate();
                                        connection.commit();
                                    } catch (final SQLException e) {
                                        System.err.println(e.getMessage());
                                        //e.printStackTrace(System.err);
                                        connection.rollback();
                                    }
                                    //connection.commit();
                                    //}
                                } catch (final Exception e) {
                                    System.err.println("Error processing line: " + line);
                                    System.err.println(e.getMessage());
                                    e.printStackTrace(System.err);
                                    throw e;
                                }

                            }
                            count++;
                        }
                    }
                } catch (final Exception e) {
                    System.err.println("Error reading file: " + path);
                    System.err.println(e.getMessage());
                    e.printStackTrace(System.err);
                    throw new RuntimeException(e);
                }
                System.out.println("Processed file: " + path);
                try {
                    Files.move(path, Paths.get(path + ".done2"));
                } catch (final IOException e) {
                    System.err.println(e.getMessage());
                    e.printStackTrace(System.err);
                    throw new RuntimeException(e);
                }
            }
        } catch (final Exception e) {
            System.err.println("Error reading folder: " + folder);
            System.err.println(e.getMessage());
            e.printStackTrace(System.err);
        }
    }


    public static void loadFull() {
        final AtomicInteger loadingCount = new AtomicInteger();
        loadingCount.set(0);

        final int maxFilesLoading = 10;


        final String folder = "F:\\Downloads\\gaia\\cdn.gea.esac.esa.int\\Gaia\\gdr3\\gaia_source";
        //final String fileName = "GaiaSource_000000-003111.csv.gz";
        //final String filePath = folder + "\\" + fileName;

        final var url = "jdbc:postgresql://localhost:5432/gaia";
        final var user = "gaia";
        final var password = "gaia";
        try (final var connection = DriverManager.getConnection(url, user, password)) {
            connection.setAutoCommit(false);

            try (final Stream<Path> paths = Files.list(Paths.get(folder)).filter(p -> p.toString().endsWith(".csv.gz"))) {

                //for (final Path path : paths.filter(Files::isRegularFile).toArray(Path[]::new)) {
                paths.forEach(path -> {

                    System.out.println("Processing file: " + path);

                    final var runnable = new Runnable() {
                        public void run() {
                            loadingCount.incrementAndGet();
                            try (final var gzip = new GZIPInputStream(new FileInputStream(path.toFile()));
                                 final var reader = new BufferedReader(new InputStreamReader(gzip))) {

                                int count = 0;
                                String line;
                                while ((line = reader.readLine()) != null) {
                                    if (!line.startsWith("#")) { // skip comments
                                        if (count > 0) { // skip header line
                                            //final String line2 = line; // for use in lambda
                                            try {
                                                final var fields = line.split(",");
                                                final var source_id = Convert.toLong(fields[2]);
                                                if (!Gaia3DRSource.sourceExists(connection, source_id)) {
                                                    final var source = new Gaia3DRSource();
                                                    //for (final var field : fields) {
                                                    //    System.out.print(field);
                                                    //    System.out.print("\t");
                                                    //}
                                                    //System.out.println();
                                                    for (int n = 0; n < Gaia3DRSource.names.length - 3; n++) {
                                                        final var fieldName = Gaia3DRSource.names[n];
                                                        final var clazz = source.getClass().getField(fieldName).getType();
                                                        //System.out.println(fieldName + " = " + fields[n]);
                                                        final var value = Convert.convert(fields[n], clazz);
                                                        source.getClass().getField(fieldName).set(source, value);
                                                        //System.out.print("\t");
                                                    }
                                                    Gaia3DRSource.insertGaia3DRSource(connection, source);
                                                    connection.commit();
                                                }
                                            } catch (final Exception e) {
                                                System.err.println("Error processing line: " + line);
                                                System.err.println(e.getMessage());
                                                e.printStackTrace(System.err);
                                                throw e;
                                            }

                                        }
                                        count++;
                                    }
                                }
                            } catch (final Exception e) {
                                System.err.println("Error reading file: " + path);
                                System.err.println(e.getMessage());
                                e.printStackTrace(System.err);
                                throw new RuntimeException(e);
                            }
                            System.out.println("Processed file: " + path);
                            try {
                                Files.move(path, Paths.get(path + ".done"));
                            } catch (final IOException e) {
                                System.err.println(e.getMessage());
                                e.printStackTrace(System.err);
                                throw new RuntimeException(e);
                            }
                            loadingCount.decrementAndGet();
                        }
                    };
                    /*
                    if (loadingCount.get() < maxFilesLoading) {
                        final var thread = new Thread(runnable);
                        thread.start();
                    } else {
                        while (loadingCount.get() >= maxFilesLoading) {
                            System.out.println("Waiting for a thread to finish...");
                            try {
                                Thread.sleep(1000 * 60 * 2);
                            } catch (final Exception e) {
                                System.err.println(e.getMessage());
                                e.printStackTrace(System.err);
                            }
                        }
                        final var thread = new Thread(runnable);
                        thread.start();
                    }

                     */
                    //final var thread = Thread.ofVirtual().start(runnable);
                    runnable.run();

                });
            } catch (final IOException e) {
                System.err.println("Error reading folder: " + folder);
                System.err.println(e.getMessage());
                e.printStackTrace(System.err);
            }
        } catch (final Exception e) {
            System.err.println("Error connecting to database: " + url);
            System.err.println(e.getMessage());
            e.printStackTrace(System.err);
        }
    }


    public static void update() {
        final String folder = "F:\\Downloads\\gaia\\cdn.gea.esac.esa.int\\Gaia\\gdr3\\gaia_source";
        final var url = "jdbc:postgresql://localhost:5432/gaia";
        final var user = "gaia";
        final var password = "gaia";
        try (final var connection = DriverManager.getConnection(url, user, password)) {
            connection.setAutoCommit(false);

            try (final Stream<Path> paths = Files.list(Paths.get(folder)).filter(p -> p.toString().endsWith(".csv.gz"))) {

                //for (final Path path : paths.filter(Files::isRegularFile).toArray(Path[]::new)) {
                paths.forEach(path -> {

                    System.out.println("Processing file: " + path);

                    final var runnable = new Runnable() {
                        public void run() {

                            try (final var gzip = new GZIPInputStream(new FileInputStream(path.toFile()));
                                 final var reader = new BufferedReader(new InputStreamReader(gzip))) {

                                int count = 0;
                                String line;
                                while ((line = reader.readLine()) != null) {
                                    if (!line.startsWith("#")) { // skip comments
                                        if (count > 0) { // skip header line
                                            //final String line2 = line; // for use in lambda
                                            try {
                                                final var fields = line.split(",");
                                                final var source_id = Convert.toLong(fields[2]);
                                                if (Gaia3DRSource.sourceExists(connection, source_id)) {
                                                    final var source = new Gaia3DRSource();
                                                    //for (final var field : fields) {
                                                    //    System.out.print(field);
                                                    //    System.out.print("\t");
                                                    //}
                                                    //System.out.println();
                                                    for (int n = 0; n < Gaia3DRSource.names.length - 3; n++) {
                                                        final var fieldName = Gaia3DRSource.names[n];
                                                        final var clazz = source.getClass().getField(fieldName).getType();
                                                        //System.out.println(fieldName + " = " + fields[n]);
                                                        final var value = Convert.convert(fields[n], clazz);
                                                        source.getClass().getField(fieldName).set(source, value);
                                                        //System.out.print("\t");
                                                    }
                                                    //Gaia3DRSource.insertGaia3DRSource(connection, source);
                                                    Gaia3DRSource.updateMisc(connection, source);
                                                    connection.commit();
                                                } else {
                                                    System.out.println("Source does not exist: " + source_id);
                                                }
                                            } catch (final Exception e) {
                                                System.err.println("Error processing line: " + line);
                                                System.err.println(e.getMessage());
                                                e.printStackTrace(System.err);
                                                throw e;
                                            }

                                        }
                                        count++;
                                    }
                                }
                            } catch (final Exception e) {
                                System.err.println("Error reading file: " + path);
                                System.err.println(e.getMessage());
                                e.printStackTrace(System.err);
                                throw new RuntimeException(e);
                            }
                            System.out.println("Processed file: " + path);
                            try {
                                Files.move(path, Paths.get(path + ".done"));
                            } catch (final IOException e) {
                                System.err.println(e.getMessage());
                                e.printStackTrace(System.err);
                                throw new RuntimeException(e);
                            }

                        }
                    };
                    runnable.run();

                });
            } catch (final IOException e) {
                System.err.println("Error reading folder: " + folder);
                System.err.println(e.getMessage());
                e.printStackTrace(System.err);
            }
        } catch (final Exception e) {
            System.err.println("Error connecting to database: " + url);
            System.err.println(e.getMessage());
            e.printStackTrace(System.err);
        }
    }


    public static void renameFiles() throws IOException {
        final String folder = "F:\\Downloads\\gaia\\cdn.gea.esac.esa.int\\Gaia\\gdr3\\gaia_source";
        try (final Stream<Path> paths = Files.list(Paths.get(folder)).filter(p -> p.toString().endsWith(".csv.gz.done"))) {

            //for (final Path path : paths.filter(Files::isRegularFile).toArray(Path[]::new)) {
            paths.forEach(path -> {

                System.out.println("Processing file: " + path);
                // remove .done from file name
                final var newPath = Paths.get(path.toString().replace(".done", ""));
                try {
                    Files.move(path, newPath);
                } catch (final IOException e) {
                    System.err.println(e.getMessage());
                    e.printStackTrace(System.err);
                }
            });
        }
    }
}
