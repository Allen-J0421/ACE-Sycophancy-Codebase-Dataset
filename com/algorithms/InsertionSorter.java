package com.algorithms;

import java.util.Comparator;
import java.util.List;

public class InsertionSorter<T> implements Sorter<T> {

    private final Comparator<T> comparator;

    public InsertionSorter(Comparator<T> comparator) {
        this.comparator = comparator;
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
                swap(list, j - 1, j);
                j--;
            }
        }
    }

    private void swap(List<T> list, int i, int j) {
        T temp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, temp);
    }
}
