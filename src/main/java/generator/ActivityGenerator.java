package generator;

import activities.FileActivity;
import activities.NetworkActivity;
import activities.ProcessActivity;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class to perform activities on the host system and generate data describing the activities.
 */
public class ActivityGenerator {
    private static final String NONE = "";

    /**
     * Creates the desired process, collects information, and kills the process.
     *
     * @param command The location of the executable command.
     * @param args    Arguments to be applied to the command.
     * @return ProcessActivity
     */
    public ProcessActivity createProcess(String command, String... args) throws IOException {
        ProcessActivity procActivity = new ProcessActivity();
        List<String> commands = new ArrayList<>();
        //TODO allow list?
        commands.add(command);
        commands.addAll(Arrays.asList(args));
        ProcessBuilder processBuilder = new ProcessBuilder(commands);
        Process process = null;
        try {
            process = processBuilder.start();
            ProcessHandle.Info pInfo = process.info();
            //if process exits immediately we won't have all the process info
            if (pInfo.command().isEmpty()) System.out.println(command + " exit code: " + process.exitValue());
            String timestamp = pInfo.startInstant().isPresent() ? pInfo.startInstant().get().toString() : NONE;
            procActivity.setTimestamp(timestamp);
            procActivity.setCommandLine(pInfo.commandLine().orElse(NONE));
            procActivity.setUser(pInfo.user().orElse(NONE));
            procActivity.setPid(String.valueOf(process.pid()));
            procActivity.setName(pInfo.command().orElse(NONE));
        } finally {
            if (process != null) process.destroy();
        }
        return procActivity;
    }

    /**
     * Creates an empty file on disk for the given Path and returns information about the operation.
     * This method does NOT delete the file created.
     *
     * @param path The path representing the file to be created.
     * @return FileActivity with information about the operation.
     */
    public FileActivity writeFile(Path path) throws IOException {
        FileActivity fileActivity = new FileActivity();
        fileActivity.setDescriptor(FileActivity.DESCRIPTOR_CREATE);
        String timestamp = Instant.now().toString();
        fileActivity.setTimestamp(timestamp);
        Path created = Files.createFile(path);
        fileActivity.setFilePath(created.toAbsolutePath().toString());
        setFileProcInfo(fileActivity);
        return fileActivity;
    }

    /**
     * Appends the modification data to the file and returns information about the operation.
     *
     * @param path         The path representing the file to be modified.
     * @param modification The data to append to the file.
     * @return FileActivity
     * @throws IOException if file IO fails (ex file doesn't exist)
     */
    public FileActivity modifyFile(Path path, byte[] modification) throws IOException {
        FileActivity fileActivity = new FileActivity();
        fileActivity.setDescriptor(FileActivity.DESCRIPTOR_MODIFY);
        String timestamp = Instant.now().toString();
        fileActivity.setTimestamp(timestamp);
        Path modified = Files.write(path, modification, StandardOpenOption.APPEND);
        fileActivity.setFilePath(modified.toAbsolutePath().toString());
        setFileProcInfo(fileActivity);
        return fileActivity;
    }

    /**
     * Deletes the specified file and returns information about the operation.
     *
     * @param path The path representing the file to be modified.
     * @return FileActivity
     * @throws IOException if file IO fails (ex file doesn't exist)
     */
    public FileActivity deleteFile(Path path) throws IOException {
        FileActivity fileActivity = new FileActivity();
        fileActivity.setDescriptor(FileActivity.DESCRIPTOR_DELETE);
        String timestamp = Instant.now().toString();
        fileActivity.setTimestamp(timestamp);
        Files.delete(path);
        fileActivity.setFilePath(path.toAbsolutePath().toString());
        setFileProcInfo(fileActivity);
        return fileActivity;
    }

    private void setFileProcInfo(FileActivity fileActivity) {
        ProcessHandle procHandle = ProcessHandle.current();
        ProcessHandle.Info pInfo = procHandle.info();
        fileActivity.setCommandLine(pInfo.commandLine().orElse(NONE));
        fileActivity.setUser(pInfo.user().orElse(NONE));
        fileActivity.setPid(String.valueOf(procHandle.pid()));
        fileActivity.setName(pInfo.command().orElse(NONE));
    }

    /**
     * Sends the specified message data to the destined network location and returns information about the operation.
     *
     * @param destIp   The destination ip address.
     * @param destPort The destination port.
     * @param protocol The protocol to be used for the network connection.
     * @param message  The data to be sent to the destination.
     * @return NetworkActivity
     * @throws IOException if network IO fails (ex can't establish connection)
     */
    public NetworkActivity connect(String destIp, int destPort, Protocol protocol, String message) throws IOException {
        NetworkActivity netActivity = new NetworkActivity();
        if (!protocol.equals(Protocol.TCP)) throw new UnsupportedOperationException("Unknown protocol " + protocol);
        String timestamp = Instant.now().toString();
        try (Socket socket = new Socket(destIp, destPort);
             OutputStream output = socket.getOutputStream();
             PrintWriter writer = new PrintWriter(output, true)) {
            writer.println(message);
            netActivity.setSource(socket.getLocalAddress().getHostAddress() + ":" + socket.getLocalPort());
        }
        ProcessHandle procHandle = ProcessHandle.current();
        ProcessHandle.Info pInfo = procHandle.info();
        netActivity.setDestination(destIp + ":" + destPort);
        netActivity.setProtocol(protocol.name());
        netActivity.setByteCount(message.getBytes().length);
        netActivity.setTimestamp(timestamp);
        netActivity.setCommandLine(pInfo.commandLine().orElse(NONE));
        netActivity.setUser(pInfo.user().orElse(NONE));
        netActivity.setPid(String.valueOf(procHandle.pid()));
        netActivity.setName(pInfo.command().orElse(NONE));
        return netActivity;
    }
}
