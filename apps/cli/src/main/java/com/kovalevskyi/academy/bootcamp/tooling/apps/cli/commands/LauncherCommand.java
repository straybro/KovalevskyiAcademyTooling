package com.kovalevskyi.academy.bootcamp.tooling.apps.cli.commands;

import com.kovalevskyi.academy.bootcamp.tooling.apps.cli.Constants;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.Callable;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.ScopeType;
import picocli.CommandLine.HelpCommand;
import picocli.CommandLine.IExecutionExceptionHandler;

@Command(mixinStandardHelpOptions = true,
        version = Constants.VERSION,
        description = "Talos the Giant Automaton",
        footer = {"", "Press Ctrl-D to exit."})
public class LauncherCommand implements Callable<Integer> {
  @CommandLine.Option(names = {"-q", "--quiet"},
      scope = ScopeType.INHERIT,
      description = "Do not output any message.",
      defaultValue = "false")
  private boolean quiet;

  @CommandLine.Option(names = {"--profile"},
      scope = ScopeType.INHERIT,
      description = "Display timing and memory usage information.",
      defaultValue = "false")
  private boolean profile;

  @CommandLine.Option(names = {"-d", "--working-dir"},
      scope = ScopeType.INHERIT,
      description = "If specified, use the given directory as working directory.")
  private String workingDirPath;

  @CommandLine.Option(names = {"-v", "--verbose"},
      scope = ScopeType.INHERIT,
      description = {"Verbose mode. 1 for normal output, 2 for more verbose output and 3 for debug",
          "Specify multiple -v options to increase verbosity. For example, `-v -v -v` or `-vvv`"})
  private boolean[] verbose = new boolean[0];

  @CommandLine.Option(names = {"--no-progress"},
      scope = ScopeType.INHERIT,
      description = "Removes the progress display that can mess with some terminals or scripts "
          + "which don't handle backspace characters.",
      defaultValue = "false")
  private boolean noProgress;

  @CommandLine.Option(names = {"--no-ansi"},
      scope = ScopeType.INHERIT,
      description = "Disable ANSI output",
      defaultValue = "false")
  public void setNoAnsi(boolean noAnsi) {
    if(noAnsi) {
      System.out.println("no ansi");
    }
  }

  private LauncherCommand() {
    super();
  }

  /**
   * Creates exception handler. It will print errors to stderr without stacktrace.
   *
   * @return Exception handler
   */
  private static IExecutionExceptionHandler createExceptionHandler() {
    return (exception, commandline, parseResult) -> {
      System.err.printf("%s is VERY unhappy!!!%n", Constants.CLI_NAME);
      System.err.println(exception.getMessage());
      return CommandLine.ExitCode.SOFTWARE;
    };
  }

  private static final CommandLine CLI = new CommandLine(new LauncherCommand())
      .setCommandName(Constants.CLI_NAME.toLowerCase())
      // .setResourceBundle(ResourceBundle.getBundle("i18n-messages"))
      // .setExecutionExceptionHandler(createExceptionHandler())
      .addSubcommand(new HelpCommand())
      .addSubcommand(new ConfigCommand())
      .addSubcommand(new FetchCommand())
      .addSubcommand(new BuildCommand())
      .addSubcommand(new SanityCommand())
      .addSubcommand(new TestCommand())
      .addSubcommand(new SubmitCommand())
      .addSubcommand(new SelfUpdateCommand())
      .addSubcommand(new DiagnoseCommand());

  @Override
  public Integer call() {
    printAsciiArt();
    CLI.usage(System.out);

    return CommandLine.ExitCode.OK;
  }

  private static void printAsciiArt() {
    var inputStream = LauncherCommand.class.getResourceAsStream("/ascii-art.txt");

    try {
      Scanner s = new Scanner(inputStream, StandardCharsets.UTF_8).useDelimiter("\\A");
      // String text = s.hasNext() ? s.next() : "";
      String text = s.next();
      System.out.println(text);
    } catch (NoSuchElementException e) {
      // logger.warn("Failed to load ASCII art.", e);
      System.err.println("Failed to load ASCII art.");
    }
  }

  public static void start(String[] args) {
    System.exit(CLI.execute(args));
  }
}
