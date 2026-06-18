public interface SortListener {
    void onSortEvent(SortEvent event);

    default void onSortStarted(String message) {
        onSortEvent(new SortEvent(SortEvent.Type.SORT_STARTED, message));
    }

    default void onSortCompleted(String message) {
        onSortEvent(new SortEvent(SortEvent.Type.SORT_COMPLETED, message));
    }

    default void onSortFailed(String message, Exception exception) {
        onSortEvent(new SortEvent(SortEvent.Type.SORT_FAILED, message, exception));
    }

    default void onValidationFailed(String message, Exception exception) {
        onSortEvent(new SortEvent(SortEvent.Type.VALIDATION_FAILED, message, exception));
    }
}

class ConsoleListener implements SortListener {
    @Override
    public void onSortEvent(SortEvent event) {
        System.out.println("[LISTENER] " + event);
    }
}

class QuietListener implements SortListener {
    @Override
    public void onSortEvent(SortEvent event) {
    }
}
