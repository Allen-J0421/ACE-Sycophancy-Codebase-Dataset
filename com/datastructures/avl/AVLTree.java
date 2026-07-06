package com.datastructures.avl;

public class AVLTree<T extends Comparable<T>> implements Tree<T> {

    private Node<T> root;
    private final InsertionStrategy<T> insertionStrategy;

    public AVLTree() {
        this(new AVLInsertionStrategy<T>(new AVLBalanceStrategy<T>()));
    }

    public AVLTree(InsertionStrategy<T> insertionStrategy) {
        this.insertionStrategy = insertionStrategy;
    }

    private static <T extends Comparable<T>> void traverse(Node<T> node, TreeVisitor<T> visitor) {
        if (node != null) {
            visitor.visit(node.key);
            traverse(node.left, visitor);
            traverse(node.right, visitor);
        }
    }

    @Override
    public void insert(T key) {
        root = insertionStrategy.insert(root, key);
    }

    @Override
    public void accept(TreeVisitor<T> visitor) {
        traverse(root, visitor);
    }
}
