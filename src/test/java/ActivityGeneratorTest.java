import activities.FileActivity;
import generator.ActivityGenerator;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ActivityGeneratorTest {

    //Happy Path Tests:

//    @Test
    //TODO need to refactor & then mock the process in the activity generator or use configurable test input for this to work across systems
//    public void testCreateProcess() throws IOException {
//        ActivityGenerator generator = new ActivityGenerator();
//        ProcessActivity procActivity = generator.createProcess("/bin/bash", "--restricted");
//        assertTrue(procActivity.getName().contains("bash"));
//        ProcessHandle procHandle =  ProcessHandle.current();
//        ProcessHandle.Info pInfo = procHandle.info();
//        assertEquals(pInfo.user().orElse(""), procActivity.getUser());
//        assertNotNull(procActivity.getPid());
//        assertFalse(procActivity.getPid().isEmpty());
//        assertEquals("/usr/bin/bash --restricted", procActivity.getCommandLine());
//        //TODO
//        assertTrue(Instant.parse(procActivity.getTimestamp()).isBefore(Instant.now()));
//        assertEquals("/usr/bin/bash", procActivity.getName());
//    }


////    @Test
//    //TODO need to refactor & then mock the socket connection in the activity generator or use configurable test input for this to work across systems
//    public void testConnect() throws IOException {
//        ActivityGenerator generator = new ActivityGenerator();
//        String message = "hello";
//        NetworkActivity netActivity;
//        int port = 44597;
//        netActivity = generator.connect("127.0.0.1", port, Protocol.TCP, message);
//        assertEquals("127.0.0.1:" + port, netActivity.getDestination());
//        //TODO
//        assertFalse(netActivity.getSource().isEmpty());
//        assertEquals(message.getBytes().length, netActivity.getByteCount());
//        assertEquals("TCP", netActivity.getProtocol());
//        ProcessHandle procHandle =  ProcessHandle.current();
//        ProcessHandle.Info pInfo = procHandle.info();
//        assertEquals(procHandle.pid(), Long.parseLong(netActivity.getPid()));
//        assertEquals(pInfo.user().orElse(""), netActivity.getUser());
//        assertEquals(pInfo.commandLine().orElse(""), netActivity.getCommandLine());
//        assertTrue(Instant.parse(netActivity.getTimestamp()).isBefore(Instant.now()));
//        assertEquals(pInfo.command().orElse(""), netActivity.getName());
//    }

    @Test
    public void testWriteFile() throws IOException {
        ActivityGenerator generator = new ActivityGenerator();
        String uuid = UUID.randomUUID().toString();
        Path testPath = Paths.get("test-file-" + uuid);
        FileActivity fileActivity;
        try {
            fileActivity = generator.writeFile(testPath);
        } catch (FileAlreadyExistsException e) {
            throw new RuntimeException(e);
        } finally {
            if (Files.exists(testPath)) Files.delete(testPath);
        }
        assertEquals(testPath.toAbsolutePath().toString(), fileActivity.getFilePath());
        assertEquals("CREATE", fileActivity.getDescriptor());
        ProcessHandle procHandle = ProcessHandle.current();
        ProcessHandle.Info pInfo = procHandle.info();
        assertEquals(procHandle.pid(), Long.parseLong(fileActivity.getPid()));
        assertEquals(pInfo.user().orElse(""), fileActivity.getUser());
        assertEquals(pInfo.commandLine().orElse(""), fileActivity.getCommandLine());
        assertTrue(Instant.parse(fileActivity.getTimestamp()).isBefore(Instant.now()));
        assertEquals(pInfo.command().orElse(""), fileActivity.getName());
    }

    @Test
    public void testModifyFile() throws IOException {
        ActivityGenerator generator = new ActivityGenerator();
        String uuid = UUID.randomUUID().toString();
        Path testPath = Paths.get("test-file-" + uuid);
        Files.createFile(testPath);
        FileActivity fileActivity;
        try {
            fileActivity = generator.modifyFile(testPath, "modified".getBytes());
        } finally {
            if (Files.exists(testPath)) Files.delete(testPath);
        }
        assertEquals(testPath.toAbsolutePath().toString(), fileActivity.getFilePath());
        assertEquals("MODIFY", fileActivity.getDescriptor());
        ProcessHandle procHandle = ProcessHandle.current();
        ProcessHandle.Info pInfo = procHandle.info();
        assertEquals(procHandle.pid(), Long.parseLong(fileActivity.getPid()));
        assertEquals(pInfo.user().orElse(""), fileActivity.getUser());
        assertEquals(pInfo.commandLine().orElse(""), fileActivity.getCommandLine());
        assertTrue(Instant.parse(fileActivity.getTimestamp()).isBefore(Instant.now()));
        assertEquals(pInfo.command().orElse(""), fileActivity.getName());
    }

    @Test
    public void testDeleteFile() throws IOException {
        ActivityGenerator generator = new ActivityGenerator();
        String uuid = UUID.randomUUID().toString();
        Path testPath = Paths.get("test-file-" + uuid);
        Files.createFile(testPath);
        FileActivity fileActivity;
        try {
            fileActivity = generator.deleteFile(testPath);
        } finally {
            if (Files.exists(testPath)) Files.delete(testPath);
        }
        assertEquals(testPath.toAbsolutePath().toString(), fileActivity.getFilePath());
        assertEquals("DELETE", fileActivity.getDescriptor());
        ProcessHandle procHandle = ProcessHandle.current();
        ProcessHandle.Info pInfo = procHandle.info();
        assertEquals(procHandle.pid(), Long.parseLong(fileActivity.getPid()));
        assertEquals(pInfo.user().orElse(""), fileActivity.getUser());
        assertEquals(pInfo.commandLine().orElse(""), fileActivity.getCommandLine());
        assertTrue(Instant.parse(fileActivity.getTimestamp()).isBefore(Instant.now()));
        assertEquals(pInfo.command().orElse(""), fileActivity.getName());
    }
}
