package sorting;

import java.util.Arrays;

public class BubbleSortTest {

    private record Case(String name, int[] input, int[] expected) {}

    private static final Case[] CASES = {
        new Case("empty",          new int[]{},                              new int[]{}),
        new Case("single",         new int[]{42},                            new int[]{42}),
        new Case("already sorted", new int[]{1, 2, 3, 4, 5},                new int[]{1, 2, 3, 4, 5}),
        new Case("reverse sorted", new int[]{5, 4, 3, 2, 1},                new int[]{1, 2, 3, 4, 5}),
        new Case("two elements",    new int[]{2, 1},                          new int[]{1, 2}),
        new Case("duplicates",     new int[]{3, 1, 2, 1, 3},                new int[]{1, 1, 2, 3, 3}),
        new Case("general",        new int[]{64, 34, 25, 12, 22, 11, 90},   new int[]{11, 12, 22, 25, 34, 64, 90}),
        new Case("negatives",      new int[]{-3, -1, -4, -1, -5, -9},       new int[]{-9, -5, -4, -3, -1, -1}),
        new Case("mixed signs",    new int[]{3, -2, 0, -1, 4},              new int[]{-2, -1, 0, 3, 4}),
    };

    public static void main(String[] args) {
        for (Case c : CASES) {
            int[] result = c.input().clone();
            BubbleSort.sort(result);
            if (!Arrays.equals(result, c.expected())) {
                throw new AssertionError(c.name() + ": expected "
                    + Arrays.toString(c.expected())
                    + " but got " + Arrays.toString(result));
            }
        }
        System.out.println("All " + CASES.length + " tests passed.");
    }
}
