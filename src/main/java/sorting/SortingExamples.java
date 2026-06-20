package sorting;

final class SortingExamples {
    private static final int[] DEMO_VALUES = {64, 34, 25, 12, 22, 11, 90};
    private static final int[] SORTED_DEMO_VALUES = {11, 12, 22, 25, 34, 64, 90};
    private static final int[] SORTED_VALUES = {1, 2, 3, 4, 5};
    private static final int[] DUPLICATE_VALUES = {3, 1, 2, 3, 1};
    private static final int[] SORTED_DUPLICATE_VALUES = {1, 1, 2, 3, 3};
    private static final int[] NEGATIVE_VALUES = {4, -2, 0, -7, 3};
    private static final int[] SORTED_NEGATIVE_VALUES = {-7, -2, 0, 3, 4};
    private static final int[] SINGLE_VALUE = {42};
    private static final int[] COPY_SORT_VALUES = {9, 4, 6, 1};
    private static final int[] SORTED_COPY_SORT_VALUES = {1, 4, 6, 9};
    private static final int[] FORMAT_VALUES = {7, 8, 9};
    private static final int[] EMPTY_VALUES = {};

    private SortingExamples() {
    }

    static int[] demoValues() {
        return DEMO_VALUES.clone();
    }

    static int[] sortedDemoValues() {
        return SORTED_DEMO_VALUES.clone();
    }

    static int[] sortedValues() {
        return SORTED_VALUES.clone();
    }

    static int[] duplicateValues() {
        return DUPLICATE_VALUES.clone();
    }

    static int[] sortedDuplicateValues() {
        return SORTED_DUPLICATE_VALUES.clone();
    }

    static int[] negativeValues() {
        return NEGATIVE_VALUES.clone();
    }

    static int[] sortedNegativeValues() {
        return SORTED_NEGATIVE_VALUES.clone();
    }

    static int[] singleValue() {
        return SINGLE_VALUE.clone();
    }

    static int[] copySortValues() {
        return COPY_SORT_VALUES.clone();
    }

    static int[] sortedCopySortValues() {
        return SORTED_COPY_SORT_VALUES.clone();
    }

    static int[] formatValues() {
        return FORMAT_VALUES.clone();
    }

    static int[] emptyValues() {
        return EMPTY_VALUES.clone();
    }
}
