import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

/**
 * Lightweight self-test for {@link RadixSort}. Cross-checks the radix sort
 * against {@link Arrays#sort} over hand-picked edge cases and many randomized
 * inputs. Run with {@code java RadixSortTest}; exits non-zero on failure.
 */
public final class RadixSortTest {

    private static int checks = 0;

    public static void main(String[] args) {
        // Edge cases.
        check(null);
        check(new int[] {});
        check(new int[] { 42 });
        check(new int[] { 2, 1 });
        check(new int[] { 5, 5, 5, 5 });
        check(new int[] { 9, 8, 7, 6, 5, 4, 3, 2, 1, 0 });
        check(new int[] { 170, 45, 75, 90, 802, 24, 2, 66 });

        // Negatives and the full int range.
        check(new int[] { -1, -2, -3, 0, 1, 2, 3 });
        check(new int[] { -802, 4, -1, 0, 75, -75 });
        check(new int[] { Integer.MIN_VALUE, Integer.MAX_VALUE, 0, -1, 1 });

        // Randomized fuzzing across a range of sizes and value spans.
        Random random = new Random(20260620L);
        for (int trial = 0; trial < 2000; trial++) {
            int length = random.nextInt(200);
            int[] data = new int[length];
            for (int i = 0; i < length; i++) {
                // Mix small and full-range values to exercise digit counts.
                data[i] = random.nextBoolean()
                        ? random.nextInt(50) - 25
                        : random.nextInt();
            }
            check(data);
        }

        // sorted() must not mutate its input.
        int[] original = { 3, 1, 2 };
        int[] result = RadixSort.sorted(original);
        require(Arrays.equals(original, new int[] { 3, 1, 2 }), "sorted() mutated its input");
        require(Arrays.equals(result, new int[] { 1, 2, 3 }), "sorted() returned wrong order");

        checkGenericApi(random);

        System.out.println("All tests passed (" + checks + " checks).");
    }

    /** An element carrying a sort key plus its original position, for stability checks. */
    private record Item(int key, int seq) {}

    /**
     * Exercises the generic key-extractor overload: correctness against a
     * reference comparator sort, and stability (equal keys keep input order)
     * across sizes that span the insertion-sort / radix boundary.
     */
    private static void checkGenericApi(Random random) {
        // Null and trivial inputs.
        require(RadixSort.sorted((Item[]) null, Item::key) == null, "sorted(null) should be null");

        // keyExtractor must be validated even though small arrays skip the work.
        try {
            RadixSort.sort(new Item[] { new Item(1, 0), new Item(2, 1) }, null);
            require(false, "null keyExtractor should throw");
        } catch (NullPointerException expected) {
            // ok
        }

        for (int trial = 0; trial < 1000; trial++) {
            int length = random.nextInt(200);
            Item[] items = new Item[length];
            for (int i = 0; i < length; i++) {
                // Narrow key span so equal keys are common, stressing stability.
                items[i] = new Item(random.nextInt(10) - 5, i);
            }

            // Ascending and descending references are both stable comparator
            // sorts, so a matching seq() order proves our sort is stable too.
            // Note: a stable descending sort keeps equal keys in INPUT order,
            // so the reference is comparingInt(...).reversed(), not the reverse
            // of the ascending result.
            checkAgainstReference(items, RadixSort.SortOrder.ASCENDING,
                    Comparator.comparingInt(Item::key));
            checkAgainstReference(items, RadixSort.SortOrder.DESCENDING,
                    Comparator.comparingInt(Item::key).reversed());
        }

        // sorted() must not mutate its input.
        Item[] src = { new Item(2, 0), new Item(1, 1) };
        Item[] sortedCopy = RadixSort.sorted(src, Item::key);
        require(src[0].key() == 2 && src[1].key() == 1, "sorted() mutated its input");
        require(sortedCopy[0].key() == 1 && sortedCopy[1].key() == 2, "sorted() wrong order");

        Item[] descCopy = RadixSort.sorted(src, Item::key, RadixSort.SortOrder.DESCENDING);
        require(descCopy[0].key() == 2 && descCopy[1].key() == 1, "sorted() desc wrong order");
    }

    /**
     * Sorts a copy of {@code items} by key in {@code order} and asserts it
     * matches {@code reference} element-for-element, including {@code seq()}
     * (which proves stability, since the reference comparator sort is stable).
     */
    private static void checkAgainstReference(Item[] items, RadixSort.SortOrder order,
                                              Comparator<Item> reference) {
        Item[] expected = items.clone();
        Arrays.sort(expected, reference);

        Item[] actual = items.clone();
        RadixSort.sort(actual, Item::key, order);

        require(expected.length == actual.length, "length changed");
        for (int i = 0; i < expected.length; i++) {
            require(expected[i].key() == actual[i].key(),
                    order + " key mismatch at " + i + " for " + Arrays.toString(items));
            require(expected[i].seq() == actual[i].seq(),
                    order + " instability at " + i + " for " + Arrays.toString(items));
        }
        checks++;
    }

    /** Sorts a copy with RadixSort and asserts it matches Arrays.sort. */
    private static void check(int[] input) {
        int[] expected = input == null ? null : Arrays.copyOf(input, input.length);
        if (expected != null) {
            Arrays.sort(expected);
        }

        int[] actual = input == null ? null : Arrays.copyOf(input, input.length);
        RadixSort.sort(actual);

        require(Arrays.equals(expected, actual),
                "ascending mismatch for " + Arrays.toString(input)
                        + "\n  expected " + Arrays.toString(expected)
                        + "\n  actual   " + Arrays.toString(actual));

        // Descending of distinct-or-equal ints is just the ascending reverse.
        int[] expectedDesc = reversed(expected);
        int[] actualDesc = input == null ? null : Arrays.copyOf(input, input.length);
        RadixSort.sort(actualDesc, RadixSort.SortOrder.DESCENDING);

        require(Arrays.equals(expectedDesc, actualDesc),
                "descending mismatch for " + Arrays.toString(input)
                        + "\n  expected " + Arrays.toString(expectedDesc)
                        + "\n  actual   " + Arrays.toString(actualDesc));
        checks++;
    }

    /** Returns a reversed copy, or {@code null} for a {@code null} input. */
    private static int[] reversed(int[] a) {
        if (a == null) {
            return null;
        }
        int[] r = new int[a.length];
        for (int i = 0; i < a.length; i++) {
            r[i] = a[a.length - 1 - i];
        }
        return r;
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            System.err.println("FAILED: " + message);
            System.exit(1);
        }
    }
}
