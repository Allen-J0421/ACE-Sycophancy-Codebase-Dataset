package com.datastructures.avl;

public interface TreeFactory<T extends Comparable<T>> {
    Tree<T> createTree();
}
