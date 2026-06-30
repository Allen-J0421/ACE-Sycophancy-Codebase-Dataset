import java.util.Comparator;

class BinarySearchTest {
    public static void main(String[] args) {
        TestRunner runner = new TestRunner("BinarySearch");

        registerNaturalOrderingTests(runner);
        registerSortednessTests(runner);
        registerSafeModeTests(runner);
        registerComparatorTests(runner);
        registerRangeTests(runner);

        runner.run();
    }

    private static void registerNaturalOrderingTests(TestRunner runner) {
        runner
                .add("finds an integer in the middle of an array", BinarySearchTest::findsIntegerValue)
                .add("returns -1 when an integer is missing",
                        BinarySearchTest::returnsMinusOneWhenIntegerValueIsMissing)
                .add("finds a string value", BinarySearchTest::findsStringValue)
                .add("finds the first element", BinarySearchTest::findsFirstElement)
                .add("finds the last element", BinarySearchTest::findsLastElement)
                .add("returns -1 for an empty array", BinarySearchTest::returnsMinusOneForEmptyArray)
                .add("finds the only element in a single-element array", BinarySearchTest::findsSingleElement)
                .add("returns -1 when a single-element array does not contain the target",
                        BinarySearchTest::returnsMinusOneForMissingSingleElement);
    }

    private static void registerSortednessTests(TestRunner runner) {
        runner
                .add("reports sorted arrays as sorted", BinarySearchTest::reportsSortedArray)
                .add("reports unsorted arrays as unsorted", BinarySearchTest::reportsUnsortedArray)
                .add("reports empty arrays as sorted", BinarySearchTest::reportsEmptyArrayAsSorted)
                .add("reports single-element arrays as sorted",
                        BinarySearchTest::reportsSingleElementArrayAsSorted);
    }

    private static void registerSafeModeTests(TestRunner runner) {
        runner
                .add("does not validate sortedness in fast search mode",
                        BinarySearchTest::doesNotValidateSortednessInFastSearchMode)
                .add("throws an exception for unsorted arrays in safe mode",
                        BinarySearchTest::throwsExceptionForUnsortedArrayInSafeMode);
    }

    private static void registerComparatorTests(TestRunner runner) {
        runner
                .add("finds an object using a custom comparator",
                        BinarySearchTest::findsObjectUsingCustomComparator)
                .add("checks sortedness using a custom comparator",
                        BinarySearchTest::checksSortednessUsingCustomComparator);
    }

    private static void registerRangeTests(TestRunner runner) {
        runner
                .add("finds an element in the full requested range", BinarySearchTest::findsElementInFullRange)
                .add("finds an element in a partial range", BinarySearchTest::findsElementInPartialRange)
                .add("returns -1 when an element is outside the requested range",
                        BinarySearchTest::returnsMinusOneWhenElementIsOutsideRange)
                .add("returns -1 for an empty requested range", BinarySearchTest::returnsMinusOneForEmptyRange)
                .add("throws when range start is greater than range end",
                        BinarySearchTest::throwsWhenRangeStartIsGreaterThanRangeEnd)
                .add("throws when range start is negative", BinarySearchTest::throwsWhenRangeStartIsNegative)
                .add("throws when range end exceeds array length",
                        BinarySearchTest::throwsWhenRangeEndExceedsArrayLength);
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

    private static void findsObjectUsingCustomComparator() {
        Person[] people = {
                new Person("Ada", 31),
                new Person("Grace", 37),
                new Person("Katherine", 42)
        };
        Comparator<Person> byAge = Comparator.comparingInt(Person::age);

        TestAssertions.assertThat(BinarySearchUtils.binarySearch(people, new Person("Target", 37), byAge))
                .isEqualTo(1, "Expected to find a Person by age using a custom comparator");
    }

    private static void checksSortednessUsingCustomComparator() {
        Person[] people = {
                new Person("Ada", 31),
                new Person("Grace", 37),
                new Person("Katherine", 42)
        };
        Comparator<Person> byAge = Comparator.comparingInt(Person::age);

        TestAssertions.assertThat(BinarySearchUtils.isSorted(people, byAge))
                .isTrue("Expected Person array to be sorted by age");
    }

    private static void findsElementInFullRange() {
        Integer[] values = {2, 3, 4, 10, 40};

        TestAssertions.assertThat(BinarySearchUtils.binarySearch(values, 10, 0, values.length))
                .isEqualTo(3, "Expected full-range search to match whole-array search");
    }

    private static void findsElementInPartialRange() {
        Integer[] values = {2, 3, 4, 10, 40};

        TestAssertions.assertThat(BinarySearchUtils.binarySearch(values, 10, 2, 4))
                .isEqualTo(3, "Expected partial-range search to find values inside the requested window");
    }

    private static void returnsMinusOneWhenElementIsOutsideRange() {
        Integer[] values = {2, 3, 4, 10, 40};

        TestAssertions.assertThat(BinarySearchUtils.binarySearch(values, 40, 0, 4))
                .isEqualTo(-1, "Values outside [fromIndex, toIndex) should not be found");
    }

    private static void returnsMinusOneForEmptyRange() {
        Integer[] values = {2, 3, 4, 10, 40};

        TestAssertions.assertThat(BinarySearchUtils.binarySearch(values, 10, 3, 3))
                .isEqualTo(-1, "Empty ranges should not contain any target");
    }

    private static void throwsWhenRangeStartIsGreaterThanRangeEnd() {
        Integer[] values = {2, 3, 4, 10, 40};

        TestAssertions.assertThrows(
                IllegalArgumentException.class,
                () -> BinarySearchUtils.binarySearch(values, 10, 4, 3),
                "Ranges with fromIndex greater than toIndex should be rejected");
    }

    private static void throwsWhenRangeStartIsNegative() {
        Integer[] values = {2, 3, 4, 10, 40};

        TestAssertions.assertThrows(
                ArrayIndexOutOfBoundsException.class,
                () -> BinarySearchUtils.binarySearch(values, 10, -1, 3),
                "Ranges with negative fromIndex should be rejected");
    }

    private static void throwsWhenRangeEndExceedsArrayLength() {
        Integer[] values = {2, 3, 4, 10, 40};

        TestAssertions.assertThrows(
                ArrayIndexOutOfBoundsException.class,
                () -> BinarySearchUtils.binarySearch(values, 10, 0, 6),
                "Ranges with toIndex beyond array length should be rejected");
    }

}

final class Person {
    private final String name;
    private final int age;

    Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    int age() {
        return age;
    }
}
