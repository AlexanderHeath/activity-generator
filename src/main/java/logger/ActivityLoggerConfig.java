package logger;

import java.nio.file.Path;

public record ActivityLoggerConfig(LogType logType, Path logPath) {

}
