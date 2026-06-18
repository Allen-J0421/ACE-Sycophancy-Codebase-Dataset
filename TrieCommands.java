import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

// Command pattern for operations
interface TrieCommand {
    OperationResult<?> execute(Trie trie);
    void undo(Trie trie);
    String getCommandName();
}

class InsertWordCommand implements TrieCommand {
    private final String word;

    InsertWordCommand(String word) {
        this.word = word;
    }

    public String getWord() {
        return word;
    }

    @Override
    public OperationResult<?> execute(Trie trie) {
        return trie.insert(word);
    }

    @Override
    public void undo(Trie trie) {
        trie.delete(word);
    }

    @Override
    public String getCommandName() {
        return "InsertWord:" + word;
    }
}

class DeleteWordCommand implements TrieCommand {
    private final String word;

    DeleteWordCommand(String word) {
        this.word = word;
    }

    public String getWord() {
        return word;
    }

    @Override
    public OperationResult<?> execute(Trie trie) {
        return trie.delete(word);
    }

    @Override
    public void undo(Trie trie) {
        trie.insert(word);
    }

    @Override
    public String getCommandName() {
        return "DeleteWord:" + word;
    }
}

class SearchWordCommand implements TrieCommand {
    private final String word;

    SearchWordCommand(String word) {
        this.word = word;
    }

    public String getWord() {
        return word;
    }

    @Override
    public OperationResult<?> execute(Trie trie) {
        boolean result = trie.search(word);
        return OperationResult.success(result);
    }

    @Override
    public void undo(Trie trie) {
        // Search is read-only, no undo needed
    }

    @Override
    public String getCommandName() {
        return "SearchWord:" + word;
    }
}

class CommandHistory {
    private final Stack<TrieCommand> undoStack = new Stack<>();
    private final Stack<TrieCommand> redoStack = new Stack<>();
    private final int maxHistorySize;

    CommandHistory() {
        this(1000);
    }

    CommandHistory(int maxHistorySize) {
        this.maxHistorySize = maxHistorySize;
    }

    void push(TrieCommand command) {
        undoStack.push(command);
        if (undoStack.size() > maxHistorySize) {
            undoStack.remove(0);
        }
        redoStack.clear();
    }

    TrieCommand undo() {
        if (undoStack.isEmpty()) {
            return null;
        }
        TrieCommand command = undoStack.pop();
        redoStack.push(command);
        return command;
    }

    TrieCommand redo() {
        if (redoStack.isEmpty()) {
            return null;
        }
        TrieCommand command = redoStack.pop();
        undoStack.push(command);
        return command;
    }

    boolean canUndo() {
        return !undoStack.isEmpty();
    }

    boolean canRedo() {
        return !redoStack.isEmpty();
    }

    int getUndoStackSize() {
        return undoStack.size();
    }

    int getRedoStackSize() {
        return redoStack.size();
    }

    void clear() {
        undoStack.clear();
        redoStack.clear();
    }
}

// Command executor with interceptor chain
interface CommandInterceptor {
    void beforeExecute(TrieCommand command);
    void afterExecute(TrieCommand command, OperationResult<?> result);
}

class CommandExecutor {
    private final List<CommandInterceptor> interceptors = new CopyOnWriteArrayList<>();
    private final CommandHistory history;

    CommandExecutor() {
        this.history = new CommandHistory();
    }

    void addInterceptor(CommandInterceptor interceptor) {
        if (interceptor != null) {
            interceptors.add(interceptor);
        }
    }

    void removeInterceptor(CommandInterceptor interceptor) {
        interceptors.remove(interceptor);
    }

    OperationResult<?> execute(TrieCommand command, Trie trie) {
        interceptors.forEach(i -> i.beforeExecute(command));
        OperationResult<?> result = command.execute(trie);
        interceptors.forEach(i -> i.afterExecute(command, result));

        if (result.isSuccess() && !(command instanceof SearchWordCommand)) {
            history.push(command);
        }

        return result;
    }

    boolean undo(Trie trie) {
        TrieCommand command = history.undo();
        if (command != null) {
            command.undo(trie);
            return true;
        }
        return false;
    }

    boolean redo(Trie trie) {
        TrieCommand command = history.redo();
        if (command != null) {
            command.execute(trie);
            return true;
        }
        return false;
    }

    CommandHistory getHistory() {
        return history;
    }
}

// Timing interceptor
class TimingInterceptor implements CommandInterceptor {
    private final Map<String, Long> executionTimes = new LinkedHashMap<>();

    @Override
    public void beforeExecute(TrieCommand command) {
        // Placeholder for timing start
    }

    @Override
    public void afterExecute(TrieCommand command, OperationResult<?> result) {
        executionTimes.put(command.getCommandName(), result.getExecutionTimeMs());
    }

    Map<String, Long> getExecutionTimes() {
        return new LinkedHashMap<>(executionTimes);
    }

    void clearTimes() {
        executionTimes.clear();
    }
}

// Validation interceptor
class ValidationInterceptor implements CommandInterceptor {
    @Override
    public void beforeExecute(TrieCommand command) {
        if (command.getCommandName().startsWith("SearchWord:")) {
            // Validate search words
        }
    }

    @Override
    public void afterExecute(TrieCommand command, OperationResult<?> result) {
        // Log validation results
    }
}
