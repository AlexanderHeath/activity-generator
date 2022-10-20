package logger;

import com.fasterxml.jackson.core.JsonProcessingException;

public class ActivityParseException extends Exception {
    public ActivityParseException(String message, JsonProcessingException e) {
        super(message, e);
    }
}
