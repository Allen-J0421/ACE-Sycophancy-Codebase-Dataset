import java.util.function.Consumer;

public interface ModernSortListener {
    void onEvent(SortEventType event);

    default void onStarted(String message) {
        onEvent(new SortStartedEvent(message));
    }

    default void onCompleted(String message, SortResult result) {
        onEvent(new SortCompletedEvent(message, result));
    }

    default void onFailed(String message, Exception cause) {
        onEvent(new SortFailedEvent(message, cause));
    }

    default void onValidationFailed(String message, Exception cause) {
        onEvent(new ValidationFailedEvent(message, cause));
    }

    static ModernSortListener of(Consumer<SortEventType> consumer) {
        return consumer::accept;
    }

    static ModernSortListener console() {
        return event -> {
            String message = switch (event) {
                case SortStartedEvent e -> "[START] " + e.message();
                case SortCompletedEvent e -> "[DONE] " + e.message() + " - " + e.result().durationMs() + "ms";
                case SortFailedEvent e -> "[FAIL] " + e.message() + " - " + e.cause().getMessage();
                case ValidationFailedEvent e -> "[INVALID] " + e.message();
            };
            System.out.println(message);
        };
    }

    static ModernSortListener quiet() {
        return event -> {
        };
    }
}
