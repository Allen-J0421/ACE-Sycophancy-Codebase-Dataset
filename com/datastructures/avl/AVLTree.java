package com.datastructures.avl;

public class AVLTree<T extends Comparable<T>> implements Tree<T> {

    private Node<T> root;
    private final BalanceStrategy<T> balanceStrategy;

    public AVLTree() {
        this(new AVLBalanceStrategy<>());
    }

    public AVLTree(BalanceStrategy<T> balanceStrategy) {
        this.balanceStrategy = balanceStrategy;
    }

    private static class TreeTraverser {

        static <T extends Comparable<T>> Node<T> insert(Node<T> node, T key, BalanceStrategy<T> strategy) {
            if (node == null)
                return new Node<>(key);

            if (key.compareTo(node.key) < 0)
                node.left = insert(node.left, key, strategy);
            else if (key.compareTo(node.key) > 0)
                node.right = insert(node.right, key, strategy);
            else
                return node;

            strategy.updateHeight(node);
            return strategy.rebalance(node, key);
        }

        static <T extends Comparable<T>> void traverse(Node<T> node, TreeVisitor<T> visitor) {
            if (node != null) {
                visitor.visit(node.key);
                traverse(node.left, visitor);
                traverse(node.right, visitor);
            }
        }
    }

    @Override
    public void insert(T key) {
        root = TreeTraverser.insert(root, key, balanceStrategy);
    }

    @Override
    public void accept(TreeVisitor<T> visitor) {
        TreeTraverser.traverse(root, visitor);
    }
}
