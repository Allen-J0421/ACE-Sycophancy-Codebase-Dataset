package com.algorithms;

import java.util.List;

@FunctionalInterface
public interface Swapper<T> {
    void swap(List<T> list, int i, int j);
}
