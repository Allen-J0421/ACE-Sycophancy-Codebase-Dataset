package com.datastructures.avl;

public interface BalanceStrategy<T extends Comparable<T>> {
    void updateHeight(Node<T> node);
    Node<T> rebalance(Node<T> node, T key);
}
