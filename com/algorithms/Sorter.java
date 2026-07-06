package com.algorithms;

import java.util.Comparator;
import java.util.List;

interface Sorter<T> {
    void sort(List<T> list, Comparator<T> comparator);
}
