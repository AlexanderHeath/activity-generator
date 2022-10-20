package activities;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public class FileActivity extends Activity {
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
