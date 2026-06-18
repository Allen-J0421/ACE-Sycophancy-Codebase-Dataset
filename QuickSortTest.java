import java.util.Arrays;

class QuickSortTest {

    private static int passedTests = 0;
    private static int totalTests = 0;

    public static void main(String[] args) {
        System.out.println("=== QuickSort Test Suite ===\n");

        testIntegerSorting();
        testStringSorting();
        testEdgeCases();
        testCustomObjects();

        System.out.println("\n=== Test Results ===");
        System.out.println("Passed: " + passedTests + "/" + totalTests);
        System.out.println(passedTests == totalTests ? "✓ All tests passed!" : "✗ Some tests failed!");
    }

    private static void testIntegerSorting() {
        System.out.println("Testing Integer Sorting:");

        testCase("Basic integers",
            new Integer[]{10, 7, 8, 9, 1, 5},
            new Integer[]{1, 5, 7, 8, 9, 10});

        testCase("Already sorted",
            new Integer[]{1, 2, 3, 4, 5},
            new Integer[]{1, 2, 3, 4, 5});

        testCase("Reverse sorted",
            new Integer[]{5, 4, 3, 2, 1},
            new Integer[]{1, 2, 3, 4, 5});

        testCase("Duplicates",
            new Integer[]{3, 1, 3, 2, 1},
            new Integer[]{1, 1, 2, 3, 3});

        testCase("Single element",
            new Integer[]{42},
            new Integer[]{42});

        testCase("Empty array",
            new Integer[]{},
            new Integer[]{});

        System.out.println();
    }

    private static void testStringSorting() {
        System.out.println("Testing String Sorting:");

        testCase("Words",
            new String[]{"zebra", "apple", "mango", "banana"},
            new String[]{"apple", "banana", "mango", "zebra"});

        testCase("Mixed case",
            new String[]{"Zoo", "apple", "Banana"},
            new String[]{"Banana", "Zoo", "apple"});

        System.out.println();
    }

    private static void testEdgeCases() {
        System.out.println("Testing Edge Cases:");

        testCase("Negative numbers",
            new Integer[]{-5, 3, -1, 0, 2},
            new Integer[]{-5, -1, 0, 2, 3});

        testCase("Two elements",
            new Integer[]{2, 1},
            new Integer[]{1, 2});

        testCase("All same elements",
            new Integer[]{5, 5, 5, 5},
            new Integer[]{5, 5, 5, 5});

        System.out.println();
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

    private static <T extends Comparable<T>> void testCase(String name, T[] input, T[] expected) {
        Sorter<T> sorter = new QuickSortImpl<>();
        sorter.sort(input);

        boolean matches = Arrays.deepEquals(input, expected);
        recordTest(matches, name);

        if (!matches) {
            System.out.println("  Expected: " + Arrays.toString(expected));
            System.out.println("  Got: " + Arrays.toString(input));
        }
    }

    private static void recordTest(boolean passed, String testName) {
        totalTests++;
        if (passed) {
            passedTests++;
            System.out.println("  ✓ " + testName);
        } else {
            System.out.println("  ✗ " + testName);
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
