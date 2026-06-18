import java.util.Objects;

public interface IntSorter {
    void sort(int[] values);

    default int[] sortedCopy(int[] values) {
        int[] sortedValues = Objects.requireNonNull(values, "values").clone();

        sort(sortedValues);

        return sortedValues;
    }
}
