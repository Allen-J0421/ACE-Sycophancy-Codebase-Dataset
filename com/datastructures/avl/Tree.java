package com.datastructures.avl;

public interface Tree<T extends Comparable<T>> {
    void insert(T key);
    void accept(TreeVisitor<T> visitor);
}
