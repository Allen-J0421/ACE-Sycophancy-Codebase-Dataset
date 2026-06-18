import java.util.OptionalInt;

public interface IntHeap {
    boolean offer(int value);

    int peek();

    int removeMin();

    default OptionalInt pollMin() {
        if (isEmpty()) {
            return OptionalInt.empty();
        }

        return OptionalInt.of(removeMin());
    }

    int removeAt(int index);

    void updateValue(int index, int newValue);

    void clear();

    boolean isEmpty();

    int size();

    int capacity();

    int valueAt(int index);

    int[] toArray();

    void addAll(int... values);

    default void addAll(IntHeap other) {
        if (other == null) {
            throw new IllegalArgumentException("other heap must not be null");
        }

        addAll(other.toArray());
    }

    default int[] drainToArray() {
        int[] drainedValues = new int[size()];
        for (int index = 0; index < drainedValues.length; index++) {
            drainedValues[index] = removeMin();
        }
        return drainedValues;
    }
}
