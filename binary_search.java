final class BinarySearch {
    static final int NOT_FOUND = -1;

    private BinarySearch() {
    }

    static int findIndex(int[] sortedValues, int target) {
        int low = 0;
        int high = sortedValues.length - 1;

        while (low <= high) {
            int middle = low + (high - low) / 2;
            int middleValue = sortedValues[middle];

            if (middleValue == target) {
                return middle;
            }

            if (middleValue < target) {
                low = middle + 1;
            } else {
                high = middle - 1;
            }
        }

        return NOT_FOUND;
    }
}

final class BinarySearchDemo {
    public static void main(String[] args) {
        for (SearchCase searchCase : searchCases()) {
            searchCase.verify();
        }

        System.out.println("All binary search checks passed.");
    }

    private static SearchCase[] searchCases() {
        int[] sampleValues = { 2, 3, 4, 10, 40 };
        int[] singleValue = { 7 };

        return new SearchCase[] {
            SearchCase.expectIndex(sampleValues, 10, 3),
            SearchCase.expectIndex(sampleValues, 5, BinarySearch.NOT_FOUND),
            SearchCase.expectIndex(new int[] {}, 10, BinarySearch.NOT_FOUND),
            SearchCase.expectIndex(singleValue, 7, 0),
            SearchCase.expectIndex(singleValue, 3, BinarySearch.NOT_FOUND)
        };
    }
}

final class SearchCase {
    private final int[] sortedValues;
    private final int target;
    private final int expectedIndex;

    private SearchCase(int[] sortedValues, int target, int expectedIndex) {
        this.sortedValues = sortedValues;
        this.target = target;
        this.expectedIndex = expectedIndex;
    }

    static SearchCase expectIndex(int[] sortedValues, int target, int expectedIndex) {
        return new SearchCase(sortedValues, target, expectedIndex);
    }

    void verify() {
        int actual = BinarySearch.findIndex(sortedValues, target);
        if (actual != expectedIndex) {
            throw new AssertionError(
                "Expected index " + expectedIndex + " for target " + target + ", but got " + actual
            );
        }
    }
}
