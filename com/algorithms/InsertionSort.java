package com.algorithms;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class InsertionSort {

    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(12, 11, 13, 5, 6);
        Sorter<Integer> sorter = new InsertionSorter<>();
        sorter.sort(numbers, Comparator.naturalOrder());
        System.out.println(numbers);
    }
}
