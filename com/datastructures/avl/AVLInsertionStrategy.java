package com.datastructures.avl;

public class AVLInsertionStrategy<T extends Comparable<T>> implements InsertionStrategy<T> {

    private final BalanceStrategy<T> balanceStrategy;

    public AVLInsertionStrategy(BalanceStrategy<T> balanceStrategy) {
        this.balanceStrategy = balanceStrategy;
    }

    @Override
    public Node<T> insert(Node<T> node, T key) {
        if (node == null)
            return new Node<>(key);

        if (key.compareTo(node.key) < 0)
            node.left = insert(node.left, key);
        else if (key.compareTo(node.key) > 0)
            node.right = insert(node.right, key);
        else
            return node;

        balanceStrategy.updateHeight(node);
        return balanceStrategy.rebalance(node, key);
    }
}
