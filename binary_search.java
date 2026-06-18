import java.util.OptionalInt;

final class BinarySearch {
    private static final int NOT_FOUND = -1;
    private static final int[] SAMPLE_VALUES = { 2, 3, 4, 10, 40 };
    private static final int SAMPLE_TARGET = 10;

    private BinarySearch() {
    }

    static int binarySearch(int[] arr, int target) {
        return search(arr, target).toLegacyIndex();
    }

    public static void main(String[] args) {
        SearchResult result = search(SAMPLE_VALUES, SAMPLE_TARGET);

        System.out.println(result.message());
    }

    private static SearchResult search(int[] arr, int target) {
        int low = 0;
        int high = arr.length - 1;

        while (low <= high) {
            int middleIndex = low + (high - low) / 2;
            int middleValue = arr[middleIndex];

            if (middleValue == target) {
                return SearchResult.foundAt(middleIndex);
            }

            if (middleValue < target) {
                low = middleIndex + 1;
            } else {
                high = middleIndex - 1;
            }
        }

        return SearchResult.notFound();
    }

    private record SearchResult(OptionalInt index) {
        private static SearchResult foundAt(int index) {
            return new SearchResult(OptionalInt.of(index));
        }

        private static SearchResult notFound() {
            return new SearchResult(OptionalInt.empty());
        }

        private int toLegacyIndex() {
            return index.orElse(NOT_FOUND);
        }

        private String message() {
            return index.isPresent()
                    ? "Element is present at index " + index.getAsInt()
                    : "Element is not present in array";
        }
    }
}
