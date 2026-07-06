package com.datastructures.avl;

public class AVLTreeFactory<T extends Comparable<T>> implements TreeFactory<T> {

    @Override
    public Tree<T> createTree() {
        return new AVLTree<>();
    }
}
