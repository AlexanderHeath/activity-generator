package logger;

import activities.Activity;

public interface ActivityLogger extends AutoCloseable {

    void logActivity(Activity activity) throws ActivityParseException;

}
