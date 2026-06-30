import java.util.Comparator;

final class BinarySearch {
    private static final SearchContainer<Integer> CONTAINER = SearchContainer.defaultContainer();

    private BinarySearch() {
    }

    static int binarySearch(int[] sortedArray, int target) {
        return binarySearch(sortedArray, target, SearchStrategies.<Integer>iterativeBinarySearch());
    }

    static int binarySearch(int[] sortedArray, int target, SearchConfiguration configuration) {
        return binarySearch(
                sortedArray,
                target,
                SearchStrategies.<Integer>iterativeBinarySearch(configuration));
    }

    static int binarySearch(int[] sortedArray, int target, SearchStrategy<Integer> strategy) {
        return SearchContainer.<Integer>naturalOrderContainer(strategy)
                .searchAlgorithm()
                .search(toIntegerArray(sortedArray), target)
                .index();
    }

    static <T extends Comparable<? super T>> int binarySearch(T[] sortedArray, T target) {
        return binarySearch(sortedArray, target, SearchStrategies.<T>iterativeBinarySearch());
    }

    static <T extends Comparable<? super T>> int binarySearch(
            T[] sortedArray,
            T target,
            SearchConfiguration configuration) {
        return binarySearch(
                sortedArray,
                target,
                SearchStrategies.<T>iterativeBinarySearch(configuration));
    }

    static <T extends Comparable<? super T>> int binarySearch(
            T[] sortedArray,
            T target,
            SearchStrategy<T> strategy) {
        return SearchContainer.<T>naturalOrderContainer(strategy)
                .searchAlgorithm()
                .search(sortedArray, target)
                .index();
    }

    static <T> int binarySearch(
            T[] sortedArray,
            T target,
            Comparator<? super T> comparator) {
        return binarySearch(
                sortedArray,
                target,
                comparator,
                SearchStrategies.<T>iterativeBinarySearch());
    }

    static <T> int binarySearch(
            T[] sortedArray,
            T target,
            Comparator<? super T> comparator,
            SearchConfiguration configuration) {
        return binarySearch(
                sortedArray,
                target,
                comparator,
                SearchStrategies.<T>iterativeBinarySearch(configuration));
    }

    static <T> int binarySearch(
            T[] sortedArray,
            T target,
            Comparator<? super T> comparator,
            SearchStrategy<T> strategy) {
        return SearchContainer.<T>comparatorContainer(comparator, strategy)
                .searchAlgorithm()
                .search(sortedArray, target)
                .index();
    }

    public static void main(String[] args) {
        BinarySearchDemo.run(CONTAINER);
    }

    private static Integer[] toIntegerArray(int[] values) {
        if (values == null) {
            throw new IllegalArgumentException("sortedArray must not be null");
        }

        Integer[] boxedValues = new Integer[values.length];
        for (int index = 0; index < values.length; index++) {
            boxedValues[index] = values[index];
        }

        return boxedValues;
    }
}
