import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Dependency-free test harness for {@link AVLTree}. Run with:
 *
 * <pre>
 *   javac AVLTree.java AVLTreeTest.java &amp;&amp; java AVLTreeTest
 * </pre>
 *
 * Exits with a non-zero status if any check fails, so it can gate a build.
 */
public class AVLTreeTest {

    private static int failures = 0;

    public static void main(String[] args) {
        demoSequenceMatchesOriginal();
        inOrderIsSorted();
        duplicatesAreIgnored();
        containsReflectsContents();
        emptyTreeBehaviour();
        nullKeyRejected();
        worksWithStrings();
        iterableYieldsAscendingOrder();
        lazyIteratorDrainsInOrderThenThrows();
        staysBalancedUnderManyInsertions();
        comparatorControlsOrdering();
        comparatorSupportsNonComparableKeys();
        comparatorIsExposed();
        nullComparatorRejected();
        naturalOrderingOnNonComparableThrows();

        if (failures == 0) {
            System.out.println("All AVL tree tests passed.");
        } else {
            System.out.println(failures + " AVL tree test(s) FAILED.");
            System.exit(1);
        }
    }

    private static void demoSequenceMatchesOriginal() {
        AVLTree<Integer> tree = newTree(10, 20, 30, 40, 50, 25);
        // Pre-order shape that the original single-file program produced.
        check("demo pre-order", Arrays.asList(30, 20, 10, 25, 40, 50), tree.preOrder());
    }

    private static void inOrderIsSorted() {
        AVLTree<Integer> tree = newTree(5, 3, 8, 1, 4, 7, 9, 2, 6);
        check("in-order sorted", Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9), tree.inOrder());
    }

    private static void duplicatesAreIgnored() {
        AVLTree<Integer> tree = newTree(1, 1, 2, 2, 2, 3);
        check("duplicate size", 3, tree.size());
        check("duplicate in-order", Arrays.asList(1, 2, 3), tree.inOrder());
    }

    private static void containsReflectsContents() {
        AVLTree<Integer> tree = newTree(15, 5, 25);
        check("contains present", true, tree.contains(25));
        check("contains absent", false, tree.contains(99));
    }

    private static void emptyTreeBehaviour() {
        AVLTree<Integer> tree = new AVLTree<>();
        check("empty isEmpty", true, tree.isEmpty());
        check("empty size", 0, tree.size());
        check("empty height", 0, tree.height());
        check("empty contains", false, tree.contains(1));
        check("empty in-order", Collections.emptyList(), tree.inOrder());
    }

    private static void nullKeyRejected() {
        AVLTree<Integer> tree = new AVLTree<>();
        try {
            tree.insert(null);
            check("null rejected", "NullPointerException", "no exception thrown");
        } catch (NullPointerException expected) {
            check("null rejected", true, true);
        }
    }

    private static void worksWithStrings() {
        AVLTree<String> tree = new AVLTree<>();
        for (String w : new String[] {"pear", "apple", "mango", "fig"}) {
            tree.insert(w);
        }
        check("string in-order", Arrays.asList("apple", "fig", "mango", "pear"), tree.inOrder());
    }

    private static void iterableYieldsAscendingOrder() {
        AVLTree<Integer> tree = newTree(3, 1, 2);
        List<Integer> seen = new ArrayList<>();
        for (int value : tree) {
            seen.add(value);
        }
        check("iterator ascending", Arrays.asList(1, 2, 3), seen);
    }

    /**
     * Exercises the lazy iterator on a larger, rotation-heavy tree and confirms
     * it drains in sorted order and then signals exhaustion correctly.
     */
    private static void lazyIteratorDrainsInOrderThenThrows() {
        AVLTree<Integer> tree = new AVLTree<>();
        List<Integer> expected = new ArrayList<>();
        for (int i = 0; i < 500; i++) {
            tree.insert(i);
            expected.add(i);
        }

        List<Integer> seen = new ArrayList<>();
        java.util.Iterator<Integer> it = tree.iterator();
        while (it.hasNext()) {
            seen.add(it.next());
        }
        check("lazy iterator full drain", expected, seen);

        try {
            it.next();
            check("lazy iterator exhausted", "NoSuchElementException", "no exception thrown");
        } catch (java.util.NoSuchElementException expectedEx) {
            check("lazy iterator exhausted", true, true);
        }
    }

    /**
     * Inserting sorted input is the worst case for an unbalanced BST (it would
     * degenerate into a linked list of height n). For an AVL tree the height
     * must stay within the proven bound of ~1.44 * log2(n + 2), which keeps
     * lookups logarithmic. This guards the balancing logic externally.
     */
    private static void staysBalancedUnderManyInsertions() {
        int n = 10_000;
        AVLTree<Integer> tree = new AVLTree<>();
        for (int i = 0; i < n; i++) {
            tree.insert(i);
        }
        check("balanced size", n, tree.size());

        int maxAllowedHeight = (int) Math.ceil(1.44 * (Math.log(n + 2) / Math.log(2)));
        boolean withinBound = tree.height() <= maxAllowedHeight;
        if (!withinBound) {
            System.out.printf("  height %d exceeds AVL bound %d for n=%d%n",
                    tree.height(), maxAllowedHeight, n);
        }
        check("balanced height within AVL bound", true, withinBound);
    }

    private static void comparatorControlsOrdering() {
        AVLTree<Integer> tree = new AVLTree<>(Comparator.reverseOrder());
        for (int key : new int[] {10, 20, 30, 5, 25}) {
            tree.insert(key);
        }
        // In-order traversal follows the comparator, so it is descending here.
        check("comparator in-order", Arrays.asList(30, 25, 20, 10, 5), tree.inOrder());
    }

    private static void comparatorSupportsNonComparableKeys() {
        // Point does not implement Comparable; ordering comes solely from the comparator.
        AVLTree<Point> tree = new AVLTree<>(
                Comparator.comparingInt((Point p) -> p.x).thenComparingInt(p -> p.y));
        tree.insert(new Point(2, 1));
        tree.insert(new Point(1, 9));
        tree.insert(new Point(2, 0));
        tree.insert(new Point(1, 9)); // duplicate by the comparator

        check("non-comparable size", 3, tree.size());
        check("non-comparable contains", true, tree.contains(new Point(2, 0)));
        List<String> ordered = new ArrayList<>();
        for (Point p : tree) {
            ordered.add(p.x + "," + p.y);
        }
        check("non-comparable order", Arrays.asList("1,9", "2,0", "2,1"), ordered);
    }

    private static void comparatorIsExposed() {
        Comparator<Integer> cmp = Comparator.reverseOrder();
        check("comparator() returns supplied", cmp, new AVLTree<>(cmp).comparator());
        check("comparator() null for natural", null, new AVLTree<Integer>().comparator());
    }

    private static void nullComparatorRejected() {
        try {
            new AVLTree<Integer>((Comparator<Integer>) null);
            check("null comparator rejected", "NullPointerException", "no exception thrown");
        } catch (NullPointerException expected) {
            check("null comparator rejected", true, true);
        }
    }

    private static void naturalOrderingOnNonComparableThrows() {
        AVLTree<Point> tree = new AVLTree<>(); // no comparator, Point is not Comparable
        tree.insert(new Point(0, 0)); // first insert never compares; the second must
        try {
            tree.insert(new Point(1, 1));
            check("natural ordering on non-comparable", "ClassCastException", "no exception thrown");
        } catch (ClassCastException expected) {
            check("natural ordering on non-comparable", true, true);
        }
    }

    /** A deliberately non-{@link Comparable} key type for comparator tests. */
    private static final class Point {
        final int x;
        final int y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    // --- helpers --------------------------------------------------------

    private static AVLTree<Integer> newTree(int... keys) {
        AVLTree<Integer> tree = new AVLTree<>();
        for (int key : keys) {
            tree.insert(key);
        }
        return tree;
    }

    private static void check(String name, Object expected, Object actual) {
        if (java.util.Objects.equals(expected, actual)) {
            System.out.println("PASS: " + name);
        } else {
            failures++;
            System.out.println("FAIL: " + name + " — expected " + expected + " but got " + actual);
        }
    }
}
