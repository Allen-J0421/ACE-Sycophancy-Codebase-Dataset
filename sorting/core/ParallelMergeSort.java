package sorting.core;

import java.util.Comparator;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

public class ParallelMergeSort<T extends Comparable<T>> {
    private final SortConfig<T> config;
    private final ForkJoinPool forkJoinPool;

    public ParallelMergeSort(SortConfig<T> config) {
        this.config = config;
        this.forkJoinPool = new ForkJoinPool(config.threadPoolSize());
    }

    public void sort(T[] array) throws SortException {
        sort(array, config.comparator());
    }

    public void sort(T[] array, Comparator<T> comparator) throws SortException {
        if (array == null || array.length == 0) {
            return;
        }

        if (config.validateInput()) {
            validateArray(array);
        }

        forkJoinPool.invoke(new MergeSortTask(array, 0, array.length - 1, comparator));
    }

    private void validateArray(T[] array) throws SortValidationException {
        if (array.length > 0 && array[0] == null) {
            throw new SortValidationException("Array contains null elements");
        }
    }

    public void shutdown() {
        forkJoinPool.shutdown();
    }

    private class MergeSortTask extends RecursiveAction {
        private final T[] array;
        private final int left;
        private final int right;
        private final Comparator<T> comparator;
        private static final int SEQUENTIAL_THRESHOLD = 1024;

        MergeSortTask(T[] array, int left, int right, Comparator<T> comparator) {
            this.array = array;
            this.left = left;
            this.right = right;
            this.comparator = comparator;
        }

        @Override
        protected void compute() {
            if (right - left < config.insertionThreshold()) {
                insertionSort(array, left, right, comparator);
                return;
            }

            if (right - left < SEQUENTIAL_THRESHOLD) {
                mergeSort(array, left, right, comparator);
            } else {
                int mid = left + (right - left) / 2;
                MergeSortTask leftTask = new MergeSortTask(array, left, mid, comparator);
                MergeSortTask rightTask = new MergeSortTask(array, mid + 1, right, comparator);

                ForkJoinTask.invokeAll(leftTask, rightTask);
                merge(array, left, mid, right, comparator);
            }
        }

        private void mergeSort(T[] array, int left, int right, Comparator<T> comparator) {
            if (left < right) {
                int mid = left + (right - left) / 2;
                mergeSort(array, left, mid, comparator);
                mergeSort(array, mid + 1, right, comparator);
                merge(array, left, mid, right, comparator);
            }
        }

        private void insertionSort(T[] array, int left, int right, Comparator<T> comparator) {
            for (int i = left + 1; i <= right; i++) {
                T key = array[i];
                int j = i - 1;
                while (j >= left && comparator.compare(array[j], key) > 0) {
                    array[j + 1] = array[j];
                    j--;
                }
                array[j + 1] = key;
            }
        }

        private void merge(T[] array, int left, int mid, int right, Comparator<T> comparator) {
            @SuppressWarnings("unchecked")
            T[] leftArr = (T[]) java.lang.reflect.Array.newInstance(
                    array.getClass().getComponentType(), mid - left + 1);
            @SuppressWarnings("unchecked")
            T[] rightArr = (T[]) java.lang.reflect.Array.newInstance(
                    array.getClass().getComponentType(), right - mid);

            System.arraycopy(array, left, leftArr, 0, mid - left + 1);
            System.arraycopy(array, mid + 1, rightArr, 0, right - mid);

            int i = 0, j = 0, k = left;
            while (i < leftArr.length && j < rightArr.length) {
                if (comparator.compare(leftArr[i], rightArr[j]) <= 0) {
                    array[k++] = leftArr[i++];
                } else {
                    array[k++] = rightArr[j++];
                }
            }

            while (i < leftArr.length) {
                array[k++] = leftArr[i++];
            }

            while (j < rightArr.length) {
                array[k++] = rightArr[j++];
            }
        }
    }
}
