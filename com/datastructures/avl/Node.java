package com.datastructures.avl;

class Node<T extends Comparable<T>> {
    T key;
    Node<T> left;
    Node<T> right;
    int height;

    Node(T k) {
        key = k;
        height = 1;
    }
}
