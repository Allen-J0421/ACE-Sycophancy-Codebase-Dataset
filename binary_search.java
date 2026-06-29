final class BinarySearch {
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

        return -1;
    }
}

final class BinarySearchDemo {
    public static void main(String[] args) {
        SearchCase[] cases = {
            new SearchCase(new int[] { 2, 3, 4, 10, 40 }, 10, 3),
            new SearchCase(new int[] { 2, 3, 4, 10, 40 }, 5, -1),
            new SearchCase(new int[] {}, 10, -1),
            new SearchCase(new int[] { 7 }, 7, 0),
            new SearchCase(new int[] { 7 }, 3, -1)
        };

        for (SearchCase searchCase : cases) {
            int actual = BinarySearch.findIndex(searchCase.sortedValues, searchCase.target);
            if (actual != searchCase.expectedIndex) {
                throw new AssertionError(
                    "Expected index " + searchCase.expectedIndex + " for target "
                        + searchCase.target + ", but got " + actual
                );
            }
        }

        System.out.println("All binary search checks passed.");
    }
}

final class SearchCase {
    final int[] sortedValues;
    final int target;
    final int expectedIndex;

    SearchCase(int[] sortedValues, int target, int expectedIndex) {
        this.sortedValues = sortedValues;
        this.target = target;
        this.expectedIndex = expectedIndex;
    }
}
