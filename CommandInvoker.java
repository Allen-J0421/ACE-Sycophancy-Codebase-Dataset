import java.util.ArrayList;
import java.util.List;

public class CommandInvoker {
  private final List<Command> history;

  public CommandInvoker() {
    this.history = new ArrayList<>();
  }

  public void execute(Command command) {
    command.execute();
    history.add(command);
  }

  public void undo() {
    if (!history.isEmpty()) {
      Command last = history.remove(history.size() - 1);
      last.undo();
    }
  }

  public int getHistorySize() {
    return history.size();
  }

  public String getLastCommandDescription() {
    if (history.isEmpty()) {
      return "No commands executed";
    }
    return history.get(history.size() - 1).getDescription();
  }

  public List<String> getCommandHistory() {
    List<String> descriptions = new ArrayList<>();
    for (Command cmd : history) {
      descriptions.add(cmd.getDescription());
    }
    return descriptions;
  }

  public void clearHistory() {
    history.clear();
  }

  @Override
  public String toString() {
    return String.format("CommandInvoker{history=%d}", history.size());
  }
}
