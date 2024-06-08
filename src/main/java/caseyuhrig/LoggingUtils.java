package caseyuhrig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;

public class LoggingUtils {

    public static void initLog4j2() {
        final LoggerContext context = LoggingUtils.configureLogging();
        final Logger logger = LogManager.getLogger(LoggingUtils.class);
        final Package log4jPackage = context.getClass().getPackage();
        final String version = log4jPackage.getImplementationVersion();
        logger.info("Log4j2 v{}, LoggerContext initialized.  Adding shutdown hook.", version);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("LoggerContext Shutdown hook triggered.");
            if (context != null) {
                logger.info("Stopping LoggerContext.");
                context.stop();
            } else {
                logger.warn("LoggerContext is null.");
            }
        }));
    }

    public static LoggerContext configureLogging() {
        final ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();

        // Pattern layout
        final String pattern = "%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n";

        // Console appender
        builder.add(builder.newAppender("Console", "CONSOLE")
                .addAttribute("target", ConsoleAppender.Target.SYSTEM_OUT)
                .add(builder.newLayout("PatternLayout")
                        .addAttribute("pattern", pattern)));

        // Rolling file appender
        builder.add(builder.newAppender("RollingFile", "RollingFile")
                .addAttribute("fileName", "logs/app.log")
                .addAttribute("filePattern", "logs/app-%d{yyyy-MM-dd}.log.gz")
                .add(builder.newLayout("PatternLayout")
                        .addAttribute("pattern", pattern))
                .addComponent(builder.newComponent("Policies")
                        .addComponent(builder.newComponent("TimeBasedTriggeringPolicy")
                                .addAttribute("interval", "1"))
                        .addComponent(builder.newComponent("SizeBasedTriggeringPolicy")
                                .addAttribute("size", "10MB"))));

        // Root logger
        builder.add(builder.newRootLogger(org.apache.logging.log4j.Level.INFO)
                .add(builder.newAppenderRef("Console"))
                .add(builder.newAppenderRef("RollingFile")));

        try {
            // If you put this in a try-with-resources block, it will close the context!
            // unless your supposed to wrap your entire program in a try-with-resources block???
            final var context = Configurator.initialize(builder.build());
            //Configurator.reconfigure(builder.build());
            //final Logger LOG = LogManager.getLogger(LoggingUtils.class);
            //LOG.info("Logging configured");
            return context;
        } catch (final Throwable throwable) {
            System.err.println("Failed to configure logging: " + throwable.getLocalizedMessage());
            throwable.printStackTrace(System.err);
        }
        return null;
    }
}
