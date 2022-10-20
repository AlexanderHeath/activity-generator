package activities;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public class NetworkActivity extends Activity {
    private String destination;
    private String source;
    private int byteCount;
    private String protocol;

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public int getByteCount() {
        return byteCount;
    }

    public void setByteCount(int byteCount) {
        this.byteCount = byteCount;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
}
