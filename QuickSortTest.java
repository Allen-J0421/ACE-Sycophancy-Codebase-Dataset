import java.util.Arrays;

class QuickSortTest {

    private static int passedTests = 0;
    private static int totalTests = 0;

    public static void main(String[] args) {
        System.out.println("=== QuickSort Test Suite ===\n");

        System.out.println("--- QuickSortImpl (Standard) ---");
        runTestsWithSorter(new QuickSortImpl<Integer>());

        System.out.println("\n--- HybridQuickSort (Optimized) ---");
        runTestsWithSorter(new HybridQuickSort<Integer>());

        System.out.println("\n--- HybridQuickSort with Median-of-Three ---");
        runTestsWithSorter(new HybridQuickSort<Integer>(new MedianOfThreePivotSelector<Integer>()));

        System.out.println("\n=== Final Test Results ===");
        System.out.println("Passed: " + passedTests + "/" + totalTests);
        System.out.println(passedTests == totalTests ? "✓ All tests passed!" : "✗ Some tests failed!");
    }

    private static void runTestsWithSorter(Sorter<Integer> intSorter) {
        testIntegerSorting(intSorter);
        testStringSorting();
        testEdgeCases(intSorter);
        testCustomObjects();
    }

    private static void testIntegerSorting(Sorter<Integer> sorter) {
        System.out.println("  Testing Integer Sorting:");

        testCase(sorter, "Basic integers",
            new Integer[]{10, 7, 8, 9, 1, 5},
            new Integer[]{1, 5, 7, 8, 9, 10});

        testCase(sorter, "Already sorted",
            new Integer[]{1, 2, 3, 4, 5},
            new Integer[]{1, 2, 3, 4, 5});

        testCase(sorter, "Reverse sorted",
            new Integer[]{5, 4, 3, 2, 1},
            new Integer[]{1, 2, 3, 4, 5});

        testCase(sorter, "Duplicates",
            new Integer[]{3, 1, 3, 2, 1},
            new Integer[]{1, 1, 2, 3, 3});

        testCase(sorter, "Single element",
            new Integer[]{42},
            new Integer[]{42});

        testCase(sorter, "Empty array",
            new Integer[]{},
            new Integer[]{});

        testCase(sorter, "Large array",
            new Integer[]{100, 50, 75, 25, 10, 90, 60, 30, 80, 40},
            new Integer[]{10, 25, 30, 40, 50, 60, 75, 80, 90, 100});
    }

    private static void testStringSorting() {
        System.out.println("  Testing String Sorting:");
        Sorter<String> stringSorter = new HybridQuickSort<String>();

        testCase(stringSorter, "Words",
            new String[]{"zebra", "apple", "mango", "banana"},
            new String[]{"apple", "banana", "mango", "zebra"});

        testCase(stringSorter, "Mixed case",
            new String[]{"Zoo", "apple", "Banana"},
            new String[]{"Banana", "Zoo", "apple"});
    }

    private static void testEdgeCases(Sorter<Integer> sorter) {
        System.out.println("  Testing Edge Cases:");

        testCase(sorter, "Negative numbers",
            new Integer[]{-5, 3, -1, 0, 2},
            new Integer[]{-5, -1, 0, 2, 3});

        testCase(sorter, "Two elements",
            new Integer[]{2, 1},
            new Integer[]{1, 2});

        testCase(sorter, "All same elements",
            new Integer[]{5, 5, 5, 5},
            new Integer[]{5, 5, 5, 5});
    }

    private static void testCustomObjects() {
        System.out.println("Testing Custom Objects:");

        Point[] points = {
            new Point(3, 4),
            new Point(1, 2),
            new Point(2, 3)
        };

        Point[] expected = {
            new Point(1, 2),
            new Point(2, 3),
            new Point(3, 4)
        };

        Sorter<Point> sorter = new QuickSortImpl<>();
        sorter.sort(points);

        boolean matches = Arrays.deepEquals(points, expected);
        recordTest(matches, "Point objects (sorted by x-coordinate)");
        if (!matches) {
            System.out.println("  Expected: " + Arrays.toString(expected));
            System.out.println("  Got: " + Arrays.toString(points));
        }

        System.out.println();
    }

    private static <T extends Comparable<T>> void testCase(Sorter<T> sorter, String name, T[] input, T[] expected) {
        T[] inputCopy = input.clone();
        sorter.sort(inputCopy);

        boolean matches = Arrays.deepEquals(inputCopy, expected);
        recordTest(matches, name);

        if (!matches) {
            System.out.println("    Expected: " + Arrays.toString(expected));
            System.out.println("    Got: " + Arrays.toString(inputCopy));
        }
    }

    private static void recordTest(boolean passed, String testName) {
        totalTests++;
        if (passed) {
            passedTests++;
            System.out.println("    ✓ " + testName);
        } else {
            System.out.println("    ✗ " + testName);
        }
    }

    static class Point implements Comparable<Point> {
        int x, y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public int compareTo(Point other) {
            return Integer.compare(this.x, other.x);
        }

        @Override
        public String toString() {
            return "(" + x + "," + y + ")";
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Point)) return false;
            Point p = (Point) obj;
            return this.x == p.x && this.y == p.y;
        }
    }
}
