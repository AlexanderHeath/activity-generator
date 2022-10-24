package activities;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public class FileActivity extends Activity {

    public static final String DESCRIPTOR_CREATE = "CREATE";
    public static final String DESCRIPTOR_MODIFY = "MODIFY";
    public static final String DESCRIPTOR_DELETE = "DELETE";
    private String filePath;
    private String descriptor;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public void setDescriptor(String descriptor) {
        this.descriptor = descriptor;
    }
}
