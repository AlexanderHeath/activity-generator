package logger;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ActivityLoggerFactory {
    private final ObjectMapper jsonMapper = new ObjectMapper();

    public ActivityLogger createLogger(ActivityLoggerConfig config) {
        LogType logType = config.logType();
        if (logType == null) throw new UnsupportedOperationException("Log type cannot be null");
        if (logType == LogType.JSON) {
            System.out.println("created json logger " + config.logPath().toAbsolutePath());
            return new JsonActivityLogger(jsonMapper, config.logPath());
        }
        throw new UnsupportedOperationException("Unknown log type " + logType);
    }
}
