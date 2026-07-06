package com.datastructures.avl;

public interface InsertionStrategy<T extends Comparable<T>> {
    Node<T> insert(Node<T> node, T key);
}
