package com.datastructures.avl;

public interface TreeVisitor<T> {
    void visit(T key);
}
