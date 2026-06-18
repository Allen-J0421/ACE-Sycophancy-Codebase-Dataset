public interface LegacyMinHeapApi extends IntHeap {
    @Deprecated(forRemoval = false)
    default boolean insertKey(int value) {
        return offer(value);
    }

    @Deprecated(forRemoval = false)
    default int getMin() {
        return peek();
    }

    @Deprecated(forRemoval = false)
    default int extractMin() {
        return pollMin().orElse(Integer.MAX_VALUE);
    }

    @Deprecated(forRemoval = false)
    default void deleteKey(int index) {
        removeAt(index);
    }

    @Deprecated(forRemoval = false)
    default void decreaseKey(int index, int newValue) {
        ensureValueMovesDownward(index, newValue);
        updateValue(index, newValue);
    }

    @Deprecated(forRemoval = false)
    default void increaseKey(int index, int newValue) {
        ensureValueMovesUpward(index, newValue);
        updateValue(index, newValue);
    }

    @Deprecated(forRemoval = false)
    default void changeValueOnAKey(int index, int newValue) {
        updateValue(index, newValue);
    }

    @Deprecated(forRemoval = false)
    default boolean isFull() {
        return size() == capacity();
    }

    private void ensureValueMovesDownward(int index, int newValue) {
        int currentValue = valueAt(index);
        if (newValue > currentValue) {
            throw new IllegalArgumentException(
                "new value " + newValue + " is greater than current value " + currentValue
            );
        }
    }

    private void ensureValueMovesUpward(int index, int newValue) {
        int currentValue = valueAt(index);
        if (newValue < currentValue) {
            throw new IllegalArgumentException(
                "new value " + newValue + " is less than current value " + currentValue
            );
        }
    }
}
