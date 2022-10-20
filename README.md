# activity-generator

"This program will allow us to test an EDR agent and ensure it generates the appropriate telemetry."

### Notes

The "create a framework" statement in the assignment instructions made me think about something that could
potentially be used in the EDR agent test-suite, such as a library. Therefore, I kept dependencies
minimal and tried to design it such that client code would have options regarding how to use the classes. However, since
the requirements mention the program triggering activities, I added a CLI for demonstration & testing (see **Main**). The CLI
could be removed, the project could be built as a library, a webservice interface could be added, etc.

The **ActivityGenerator** and **JsonActivityLogger** are the core classes.
The **Activity** models serve as both the contract between modules and as a schema for data types of the log file.
If a new field is to be added to the log file, it can be added to the model and set in the Generator. The Logger does
not need to change. I chose the approach of using an existing object mapper for speed of development and the fact that
Jackson library has support for csv, yaml, & others. Therefore, support for additional log file types could be added easily.



### Build & Run

**JDK/JRE 17+** & **maven** are required to build the project.
<br><br>
executing

```bash
mvn package
```

or, if necessary

````bash
mvn package -Dmaven.test.skip
````

from the root project directory will generate **activity-generator-1.0-SNAPSHOT-jar-with-dependencies.jar** in the **./target** directory.

The jar file can be executed like so:

```bash
java -jar activity-generator-1.0-SNAPSHOT-jar-with-dependencies.jar -p /bin/bash --restricted -n 127.0.0.1 44001 testmessage -w testfile.txt -m testfile.txt -d testfile.txt
```

This will create an **activity-log.json** file in the same directory as the program. The location and name of the
log file cannot currently be specified by the CLI options.
Sample log file content generated from the above command:

```json
[{"timestamp":"2022-10-19T20:25:20.840Z","commandLine":"/usr/bin/bash --restricted","user":"alexander","pid":"61513","name":"/usr/bin/bash"},
  {"timestamp":"2022-10-19T20:25:21.129933796Z","commandLine":"/usr/lib/jvm/java-18-openjdk/bin/java -jar activity-generator-1.0-SNAPSHOT-jar-with-dependencies.jar -p /bin/bash --restricted -n 127.0.0.1 44001 testtest -w testfile.txt -m testfile.txt -d testfile.txt","user":"alexander","pid":"61492","name":"/usr/lib/jvm/java-18-openjdk/bin/java","filePath":"/home/alexander/workspace/IdeaProjects/activity-generator/target/testfile.txt","descriptor":"CREATE"},
  {"timestamp":"2022-10-19T20:25:21.131228811Z","commandLine":"/usr/lib/jvm/java-18-openjdk/bin/java -jar activity-generator-1.0-SNAPSHOT-jar-with-dependencies.jar -p /bin/bash --restricted -n 127.0.0.1 44001 testtest -w testfile.txt -m testfile.txt -d testfile.txt","user":"alexander","pid":"61492","name":"/usr/lib/jvm/java-18-openjdk/bin/java","filePath":"/home/alexander/workspace/IdeaProjects/activity-generator/target/testfile.txt","descriptor":"MODIFY"},
  {"timestamp":"2022-10-19T20:25:21.131866645Z","commandLine":"/usr/lib/jvm/java-18-openjdk/bin/java -jar activity-generator-1.0-SNAPSHOT-jar-with-dependencies.jar -p /bin/bash --restricted -n 127.0.0.1 44001 testtest -w testfile.txt -m testfile.txt -d testfile.txt","user":"alexander","pid":"61492","name":"/usr/lib/jvm/java-18-openjdk/bin/java","filePath":"/home/alexander/workspace/IdeaProjects/activity-generator/target/testfile.txt","descriptor":"DELETE"},
  {"timestamp":"2022-10-19T20:25:21.132494578Z","commandLine":"/usr/lib/jvm/java-18-openjdk/bin/java -jar activity-generator-1.0-SNAPSHOT-jar-with-dependencies.jar -p /bin/bash --restricted -n 127.0.0.1 44001 testtest -w testfile.txt -m testfile.txt -d testfile.txt","user":"alexander","pid":"61492","name":"/usr/lib/jvm/java-18-openjdk/bin/java","destination":"127.0.0.1:44001","source":"127.0.0.1:60784","byteCount":11,"protocol":"TCP"}]
```

The CLI options & descriptions are as follows:

```
options: p, n, w, m, d
usage: activity-generator
 -d,--delete-file <path>   Delete a file.
 -m,--modify-file <path>   Modify a file.
 -n,--network <message>    Create a network connection and send message.
                           Takes destination ip, destination port, and
                           message arguments.
 -p,--process <args>       Run an executable. Takes an executable command
                           or path and optional command-line arguments.
 -w,--write-file <path>    Create a file.
```

#### TODOs

- [ ] application logging framework / facade
- [ ] consider allow list for executable files
- [ ] review error handling
- [ ] integration tests
- [ ] considerations for log roll over, safeguards for creating two loggers with same log file path
- [ ] changes are needed for some tests to be system independent
- [ ] mac testing

#### Known Issues

- windows command line output missing (java bug w/ work around shared https://stackoverflow.com/a/54790608) 
