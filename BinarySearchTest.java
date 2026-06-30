class BinarySearchTest {
    private static int passedTests = 0;

    public static void main(String[] args) {
        runTest("finds an integer in the middle of an array", BinarySearchTest::findsIntegerValue);
        runTest("returns -1 when an integer is missing", BinarySearchTest::returnsMinusOneWhenIntegerValueIsMissing);
        runTest("finds a string value", BinarySearchTest::findsStringValue);
        runTest("finds the first element", BinarySearchTest::findsFirstElement);
        runTest("finds the last element", BinarySearchTest::findsLastElement);
        runTest("returns -1 for an empty array", BinarySearchTest::returnsMinusOneForEmptyArray);
        runTest("finds the only element in a single-element array", BinarySearchTest::findsSingleElement);
        runTest("returns -1 when a single-element array does not contain the target",
                BinarySearchTest::returnsMinusOneForMissingSingleElement);
        runTest("reports sorted arrays as sorted", BinarySearchTest::reportsSortedArray);
        runTest("reports unsorted arrays as unsorted", BinarySearchTest::reportsUnsortedArray);
        runTest("reports empty arrays as sorted", BinarySearchTest::reportsEmptyArrayAsSorted);
        runTest("reports single-element arrays as sorted", BinarySearchTest::reportsSingleElementArrayAsSorted);
        runTest("does not validate sortedness in fast search mode",
                BinarySearchTest::doesNotValidateSortednessInFastSearchMode);
        runTest("throws an exception for unsorted arrays in safe mode",
                BinarySearchTest::throwsExceptionForUnsortedArrayInSafeMode);

        System.out.println(passedTests + " BinarySearch tests passed.");
    }

    private static void findsIntegerValue() {
        Integer[] values = {2, 3, 4, 10, 40};

        TestAssertions.assertThat(BinarySearchUtils.binarySearch(values, 10))
                .isEqualTo(3, "Expected to find 10 at its sorted index");
    }

    private static void returnsMinusOneWhenIntegerValueIsMissing() {
        Integer[] values = {2, 3, 4, 10, 40};

        TestAssertions.assertThat(BinarySearchUtils.binarySearch(values, 5))
                .isEqualTo(-1, "Missing values should return -1");
    }

    private static void findsStringValue() {
        String[] values = {"alpha", "bravo", "charlie", "delta"};

        TestAssertions.assertThat(BinarySearchUtils.binarySearch(values, "charlie"))
                .isEqualTo(2, "Expected generic search to support String values");
    }

    private static void findsFirstElement() {
        Integer[] values = {2, 3, 4, 10, 40};

        TestAssertions.assertThat(BinarySearchUtils.binarySearch(values, 2))
                .isEqualTo(0, "Expected to find the first element at index 0");
    }

    private static void findsLastElement() {
        Integer[] values = {2, 3, 4, 10, 40};

        TestAssertions.assertThat(BinarySearchUtils.binarySearch(values, 40))
                .isEqualTo(4, "Expected to find the last element at the final index");
    }

    private static void returnsMinusOneForEmptyArray() {
        Integer[] values = {};

        TestAssertions.assertThat(BinarySearchUtils.binarySearch(values, 10))
                .isEqualTo(-1, "Empty arrays should not contain any target");
    }

    private static void findsSingleElement() {
        Integer[] values = {10};

        TestAssertions.assertThat(BinarySearchUtils.binarySearch(values, 10))
                .isEqualTo(0, "Expected to find the only element at index 0");
    }

    private static void returnsMinusOneForMissingSingleElement() {
        Integer[] values = {10};

        TestAssertions.assertThat(BinarySearchUtils.binarySearch(values, 5))
                .isEqualTo(-1, "Single-element arrays should return -1 when the target is absent");
    }

    private static void reportsSortedArray() {
        Integer[] values = {2, 3, 4, 10, 40};

        TestAssertions.assertThat(BinarySearchUtils.isSorted(values))
                .isTrue("Sorted arrays should be reported as sorted");
    }

    private static void reportsUnsortedArray() {
        Integer[] values = {2, 10, 4, 40};

        TestAssertions.assertThat(BinarySearchUtils.isSorted(values))
                .isFalse("Unsorted arrays should be reported as unsorted");
    }

    private static void reportsEmptyArrayAsSorted() {
        Integer[] values = {};

        TestAssertions.assertThat(BinarySearchUtils.isSorted(values))
                .isTrue("Empty arrays should be treated as sorted");
    }

    private static void reportsSingleElementArrayAsSorted() {
        Integer[] values = {10};

        TestAssertions.assertThat(BinarySearchUtils.isSorted(values))
                .isTrue("Single-element arrays should be treated as sorted");
    }

    private static void doesNotValidateSortednessInFastSearchMode() {
        Integer[] values = {2, 10, 4, 40};

        TestAssertions.assertThat(BinarySearchUtils.binarySearch(values, 10))
                .isEqualTo(1, "Fast search mode should not reject unsorted arrays");
    }

    private static void throwsExceptionForUnsortedArrayInSafeMode() {
        Integer[] values = {2, 10, 4, 40};

        TestAssertions.assertThrows(
                IllegalArgumentException.class,
                () -> BinarySearchUtils.binarySearch(values, 10, true),
                "Safe mode should reject unsorted arrays before searching");
    }

    private static void runTest(String name, Runnable test) {
        try {
            test.run();
            passedTests++;
        } catch (AssertionError error) {
            throw new AssertionError("Test failed: " + name + System.lineSeparator() + error.getMessage(), error);
        }
    }
}
