package com.algorithms;

import java.util.Comparator;
import java.util.List;

class InsertionSorter<T> implements Sorter<T> {

    @Override
    public void sort(List<T> list, Comparator<T> comparator) {
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
