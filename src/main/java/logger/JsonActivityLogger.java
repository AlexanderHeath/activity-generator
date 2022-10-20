package logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class JsonActivityLogger implements ActivityLogger {
    private final ObjectMapper mapper;
    private final BufferedWriter writer;
    private final Path logFile;
    private boolean firstLine = true;

    //TODO considerations for log roll over, safeguards for creating two loggers with same log file path, etc
    public JsonActivityLogger(ObjectMapper mapper, Path logFile) {
        this.mapper = mapper;
        try {
            this.logFile = logFile;
            FileWriter fw = new FileWriter(logFile.toAbsolutePath().toString());
            this.writer = new BufferedWriter(fw);
            this.writer.write("[");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Log writes are not synchronized by this class. This method should be called serially.
     *
     * @param activity data to be written to the log
     */
    @Override
    public void logActivity(Object activity) throws ActivityParseException {
        try {
            String actString = mapper.writeValueAsString(activity);
            if (!firstLine) {
                writer.write(",");
                writer.newLine();
            }
            writer.write(actString);
            writer.flush();
            firstLine = false;
        } catch (JsonProcessingException e) {
            throw new ActivityParseException("Unable to parse activity data", e);
        } catch (IOException e) {
            throw new RuntimeException("I/O exception writing to log file " + logFile.toString(), e);
        }
    }

    /**
     * Terminates the json list and closes resources.
     */
    @Override
    public void close() {
        if (writer != null) {
            try {
                writer.write("]");
            } catch (IOException e) {
                throw new RuntimeException("I/O exception writing to log file " + logFile.toString(), e);
            }
            try {
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException("Failed to close log file writer", e);
            }
        }
    }
}
