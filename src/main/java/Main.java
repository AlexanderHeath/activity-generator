import activities.Activity;
import generator.ActivityGenerator;
import generator.Protocol;
import logger.*;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.System.exit;

public class Main {

    /**
     * CLI for DEMO & testing purposes. Creates and logs a single process, create file, modify file,
     * delete file, and network connection activity
     *
     * @param args CLI options
     */
    public static void main(String... args) {
        Options options = makeOptions();
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("activity-generator", options);
            exit(1);
        }
        String[] procArgs = cmd.getOptionValues("process");
        String[] netArgs = cmd.getOptionValues("network");
        String[] writeArgs = cmd.getOptionValues("write-file");
        String[] modifyArgs = cmd.getOptionValues("modify-file");
        String[] deleteArgs = cmd.getOptionValues("delete-file");
        ActivityGenerator generator = new ActivityGenerator();
        List<Activity> activities = new ArrayList<>();
        try {
            activities.add(generator.createProcess(procArgs[0], Arrays.copyOfRange(procArgs, 1, procArgs.length)));
        } catch (IOException e) {
            System.err.println("Could not create process activity: " + e);
        }
        try {
            activities.add(generator.writeFile(Paths.get(writeArgs[0])));
        } catch (IOException e) {
            System.err.println("Could not create file write activity: " + e);
        }
        try {
            activities.add(generator.modifyFile(Paths.get(modifyArgs[0]), "appended".getBytes()));
        } catch (IOException e) {
            System.err.println("Could not create file modification activity: " + e);
        }
        try {
            activities.add(generator.deleteFile(Paths.get(deleteArgs[0])));
        } catch (IOException e) {
            System.err.println("Could not create file deletion activity: " + e);
        }
        try {
            activities.add(generator.connect(netArgs[0], Integer.parseInt(netArgs[1]), Protocol.TCP, netArgs[2]));
        } catch (IOException e) {
            System.err.println("Could not create network activity: " + e);
        }
        ActivityLoggerConfig loggerConfig = new ActivityLoggerConfig(LogType.JSON, Paths.get("activity-log.json"));
        ActivityLoggerFactory loggerFactory = new ActivityLoggerFactory();
        try (ActivityLogger logger = loggerFactory.createLogger(loggerConfig)) {
            for (Activity activity : activities) {
                try {
                    logger.logActivity(activity);
                } catch (ActivityParseException e) {
                    System.err.println(e.getMessage());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Options makeOptions() {
        Options options = new Options();
        Option pInput = Option.builder("p")
                .longOpt("process")
                .argName("command")
                .argName("args")
                .hasArgs()
                .desc("Run an executable. Takes an executable command or path and optional command-line arguments.")
                .required()
                .build();
        options.addOption(pInput);

        Option nInput = Option.builder("n")
                .longOpt("network")
                .numberOfArgs(3)
                .argName("destIp")
                .argName("destPort")
                .argName("message")
                .desc("Create a network connection and send message. Takes destination ip, destination port, and message arguments.")
                .required()
                .build();
        options.addOption(nInput);

        Option wInput = Option.builder("w")
                .longOpt("write-file")
                .argName("path")
                .hasArg()
                .desc("Create a file.")
                .required()
                .build();
        options.addOption(wInput);

        Option mInput = Option.builder("m")
                .longOpt("modify-file")
                .argName("path")
                .hasArg()
                .desc("Modify a file.")
                .required()
                .build();
        options.addOption(mInput);

        Option dInput = Option.builder("d")
                .longOpt("delete-file")
                .argName("path")
                .hasArg()
                .desc("Delete a file.")
                .required()
                .build();
        options.addOption(dInput);
        return options;
    }
}
