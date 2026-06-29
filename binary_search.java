final class BinarySearch {
    private static final int NOT_FOUND = -1;
    private static final int[] SAMPLE_VALUES = { 2, 3, 4, 10, 40 };
    private static final int SAMPLE_TARGET = 10;

    private BinarySearch() {
    }

    static int binarySearch(int[] sortedValues, int target) {
        return search(sortedValues, target).toIndex();
    }

    private static SearchResult search(int[] sortedValues, int target) {
        int lowerBound = 0;
        int upperBound = sortedValues.length - 1;

        while (lowerBound <= upperBound) {
            int candidateIndex = midpoint(lowerBound, upperBound);
            int candidateValue = sortedValues[candidateIndex];

            if (candidateValue == target) {
                return SearchResult.found(candidateIndex);
            }

            if (candidateValue < target) {
                lowerBound = candidateIndex + 1;
            } else {
                upperBound = candidateIndex - 1;
            }
        }

        return SearchResult.notFound();
    }

    public static void main(String[] args) {
        runSampleSearch();
    }

    private static void runSampleSearch() {
        SearchResult result = search(SAMPLE_VALUES, SAMPLE_TARGET);

        System.out.println(result.message());
    }

    private static int midpoint(int lowerBound, int upperBound) {
        return lowerBound + (upperBound - lowerBound) / 2;
    }

    private static final class SearchResult {
        private final int index;

        private SearchResult(int index) {
            this.index = index;
        }

        static SearchResult found(int index) {
            return new SearchResult(index);
        }

        static SearchResult notFound() {
            return new SearchResult(NOT_FOUND);
        }

        int toIndex() {
            return index;
        }

        String message() {
            if (!isFound()) {
                return "Element is not present in array";
            }

            return "Element is present at index " + index;
        }

        private boolean isFound() {
            return index != NOT_FOUND;
        }
    }
}
