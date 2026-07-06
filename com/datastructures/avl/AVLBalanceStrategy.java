package com.datastructures.avl;

public class AVLBalanceStrategy<T extends Comparable<T>> implements BalanceStrategy<T> {

    private int height(Node<T> n) {
        return n == null ? 0 : n.height;
    }

    private int getBalance(Node<T> n) {
        return n == null ? 0 : height(n.left) - height(n.right);
    }

    @Override
    public void updateHeight(Node<T> n) {
        n.height = 1 + Math.max(height(n.left), height(n.right));
    }

    private Node<T> rightRotate(Node<T> y) {
        Node<T> x = y.left;
        Node<T> subtree = x.right;

        x.right = y;
        y.left = subtree;

        updateHeight(y);
        updateHeight(x);

        return x;
    }

    private Node<T> leftRotate(Node<T> x) {
        Node<T> y = x.right;
        Node<T> subtree = y.left;

        y.left = x;
        x.right = subtree;

        updateHeight(x);
        updateHeight(y);

        return y;
    }

    @Override
    public Node<T> rebalance(Node<T> node, T key) {
        int balance = getBalance(node);

        if (balance > 1 && key.compareTo(node.left.key) < 0)
            return rightRotate(node);

        if (balance < -1 && key.compareTo(node.right.key) > 0)
            return leftRotate(node);

        if (balance > 1 && key.compareTo(node.left.key) > 0) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        if (balance < -1 && key.compareTo(node.right.key) < 0) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return node;
    }
}
