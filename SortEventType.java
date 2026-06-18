public sealed interface SortEventType permits
        SortStartedEvent,
        SortCompletedEvent,
        SortFailedEvent,
        ValidationFailedEvent {
    String typeName();
}

record SortStartedEvent(String message) implements SortEventType {
    @Override
    public String typeName() {
        return "SORT_STARTED";
    }
}

record SortCompletedEvent(String message, SortResult result) implements SortEventType {
    @Override
    public String typeName() {
        return "SORT_COMPLETED";
    }
}

record SortFailedEvent(String message, Exception cause) implements SortEventType {
    @Override
    public String typeName() {
        return "SORT_FAILED";
    }
}

record ValidationFailedEvent(String message, Exception cause) implements SortEventType {
    @Override
    public String typeName() {
        return "VALIDATION_FAILED";
    }
}
