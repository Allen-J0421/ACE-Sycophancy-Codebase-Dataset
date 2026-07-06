package com.algorithms;

import java.util.Comparator;
import java.util.List;

public class InsertionSorter<T> implements Sorter<T> {

    private final Comparator<T> comparator;
    private final Swapper<T> swapper;

    public InsertionSorter(Comparator<T> comparator) {
        this(comparator, (list, i, j) -> {
            T temp = list.get(i);
            list.set(i, list.get(j));
            list.set(j, temp);
        });
    }

    public InsertionSorter(Comparator<T> comparator, Swapper<T> swapper) {
        this.comparator = comparator;
        this.swapper = swapper;
    }

    public static <T extends Comparable<T>> InsertionSorter<T> naturalOrder() {
        return new InsertionSorter<T>(Comparator.naturalOrder());
    }

    public static <T extends Comparable<T>> InsertionSorter<T> reverseOrder() {
        return new InsertionSorter<T>(Comparator.reverseOrder());
    }

    @Override
    public void sort(List<T> list) {
        for (int i = 1; i < list.size(); i++) {
            int j = i;
            while (j > 0 && comparator.compare(list.get(j - 1), list.get(j)) > 0) {
                swapper.swap(list, j - 1, j);
                j--;
            }
        }
    }
}
