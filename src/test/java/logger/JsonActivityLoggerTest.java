package logger;

import activities.FileActivity;
import activities.NetworkActivity;
import activities.ProcessActivity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonActivityLoggerTest {
    @Test
    public void testLogActivityHappyPath() throws IOException, ActivityParseException {
        String timestamp = "2022-10-18T21:47:17.145048019Z";
        ProcessActivity procActivity = new ProcessActivity();
        procActivity.setName("/bin/bash");
        procActivity.setCommandLine("/bin/bash");
        procActivity.setUser("alexander");
        procActivity.setTimestamp(timestamp);
        procActivity.setPid("2987");

        NetworkActivity netActivity = new NetworkActivity();
        netActivity.setByteCount(10);
        netActivity.setProtocol("TCP");
        netActivity.setDestination("10.11.12.1:443");
        netActivity.setSource("localhost:30003");
        netActivity.setName("java");
        netActivity.setPid("2020");
        netActivity.setCommandLine("java --options");
        netActivity.setTimestamp(timestamp);
        netActivity.setUser("root");

        FileActivity writeActivity = new FileActivity();
        writeActivity.setFilePath("/test123.txt");
        writeActivity.setDescriptor("CREATE");
        writeActivity.setName("java");
        writeActivity.setPid("2020");
        writeActivity.setCommandLine("java --options");
        writeActivity.setTimestamp(timestamp);
        writeActivity.setUser("root");

        FileActivity modifyActivity = new FileActivity();
        modifyActivity.setFilePath("/test123.txt");
        modifyActivity.setDescriptor("MODIFY");
        modifyActivity.setName("java");
        modifyActivity.setPid("2020");
        modifyActivity.setCommandLine("java --options");
        modifyActivity.setTimestamp(timestamp);
        modifyActivity.setUser("root");

        FileActivity deleteActivity = new FileActivity();
        deleteActivity.setFilePath("/test123.txt");
        deleteActivity.setDescriptor("DELETE");
        deleteActivity.setName("java");
        deleteActivity.setPid("2020");
        deleteActivity.setCommandLine("java --options");
        deleteActivity.setTimestamp(timestamp);
        deleteActivity.setUser("root");

        Path path = Paths.get("./activity-log.json");
        try (JsonActivityLogger logger = new JsonActivityLogger(new ObjectMapper(), path)) {
            logger.logActivity(procActivity);
            logger.logActivity(netActivity);
            logger.logActivity(writeActivity);
            logger.logActivity(modifyActivity);
            logger.logActivity(deleteActivity);
        }
        List<String> lines = Files.readAllLines(path);
        assertEquals(5, lines.size());
        assertEquals("[{\"timestamp\":\"2022-10-18T21:47:17.145048019Z\",\"commandLine\":\"/bin/bash\",\"user\":\"alexander\",\"pid\":\"2987\",\"name\":\"/bin/bash\"},", lines.get(0));
        assertEquals("{\"timestamp\":\"2022-10-18T21:47:17.145048019Z\",\"commandLine\":\"java --options\",\"user\":\"root\",\"pid\":\"2020\",\"name\":\"java\",\"destination\":\"10.11.12.1:443\",\"source\":\"localhost:30003\",\"byteCount\":10,\"protocol\":\"TCP\"},", lines.get(1));
        assertEquals("{\"timestamp\":\"2022-10-18T21:47:17.145048019Z\",\"commandLine\":\"java --options\",\"user\":\"root\",\"pid\":\"2020\",\"name\":\"java\",\"filePath\":\"/test123.txt\",\"descriptor\":\"CREATE\"},", lines.get(2));
        assertEquals("{\"timestamp\":\"2022-10-18T21:47:17.145048019Z\",\"commandLine\":\"java --options\",\"user\":\"root\",\"pid\":\"2020\",\"name\":\"java\",\"filePath\":\"/test123.txt\",\"descriptor\":\"MODIFY\"},", lines.get(3));
        assertEquals("{\"timestamp\":\"2022-10-18T21:47:17.145048019Z\",\"commandLine\":\"java --options\",\"user\":\"root\",\"pid\":\"2020\",\"name\":\"java\",\"filePath\":\"/test123.txt\",\"descriptor\":\"DELETE\"}]", lines.get(4));
    }
}
