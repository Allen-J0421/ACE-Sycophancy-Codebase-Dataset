package sorting.core;

import java.util.Comparator;

@FunctionalInterface
public interface SortOperation<T extends Comparable<T>> {
    void execute(T[] array, Comparator<T> comparator) throws SortException;

    static <T extends Comparable<T>> SortOperation<T> safe(SortOperation<T> operation) {
        return (array, comparator) -> {
            try {
                operation.execute(array, comparator);
            } catch (SortException e) {
                throw e;
            } catch (Exception e) {
                throw new SortExecutionException("Unexpected error during sort", e);
            }
        };
    }

    static <T extends Comparable<T>> SortOperation<T> validated(SortOperation<T> operation) {
        return (array, comparator) -> {
            if (array == null) {
                throw new SortValidationException("Array cannot be null");
            }
            operation.execute(array, comparator);
        };
    }

    default SortOperation<T> andThen(SortOperation<T> next) {
        return (array, comparator) -> {
            this.execute(array, comparator);
            next.execute(array, comparator);
        };
    }
}
