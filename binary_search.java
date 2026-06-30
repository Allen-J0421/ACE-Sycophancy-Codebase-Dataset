import java.util.Comparator;

final class BinarySearch {
    private static final SearchContainer<Integer> CONTAINER = SearchContainer.defaultContainer();

    private BinarySearch() {
    }

    static int binarySearch(int[] sortedArray, int target) {
        return CONTAINER.searchAlgorithm().search(toIntegerArray(sortedArray), target).index();
    }

    static <T extends Comparable<? super T>> int binarySearch(T[] sortedArray, T target) {
        return SearchContainer.<T>naturalOrderContainer()
                .searchAlgorithm()
                .search(sortedArray, target)
                .index();
    }

    static <T> int binarySearch(
            T[] sortedArray,
            T target,
            Comparator<? super T> comparator) {
        return SearchContainer.<T>comparatorContainer(comparator)
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
