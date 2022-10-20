package logger;

public interface ActivityLogger extends AutoCloseable {

    void logActivity(Object activity) throws ActivityParseException;

}
