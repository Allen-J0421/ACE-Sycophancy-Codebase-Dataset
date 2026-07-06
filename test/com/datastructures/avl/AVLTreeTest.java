package com.datastructures.avl;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AVLTreeTest {

    private AVLTree<Integer> tree;

    @Before
    public void setUp() {
        tree = new AVLTree<>();
    }

    @Test
    public void testEmptyTreeProducesNoVisits() {
        List<Integer> result = new ArrayList<>();
        tree.accept(result::add);

        assertTrue(result.isEmpty());
    }

    @Test
    public void testSingleInsert() {
        tree.insert(42);

        List<Integer> result = new ArrayList<>();
        tree.accept(result::add);

        assertEquals(List.of(42), result);
    }

    @Test
    public void testDuplicateInsertIsIgnored() {
        tree.insert(10);
        tree.insert(10);

        List<Integer> result = new ArrayList<>();
        tree.accept(result::add);

        assertEquals(List.of(10), result);
    }

    @Test
    public void testPreOrderAfterSequentialInsertions() {
        tree.insert(10);
        tree.insert(20);
        tree.insert(30);
        tree.insert(40);
        tree.insert(50);
        tree.insert(25);

        List<Integer> result = new ArrayList<>();
        tree.accept(result::add);

        assertEquals(List.of(30, 20, 10, 25, 40, 50), result);
    }

    @Test
    public void testLeftLeftRotation() {
        tree.insert(30);
        tree.insert(20);
        tree.insert(10);

        List<Integer> result = new ArrayList<>();
        tree.accept(result::add);

        assertEquals(List.of(20, 10, 30), result);
    }

    @Test
    public void testRightRightRotation() {
        tree.insert(10);
        tree.insert(20);
        tree.insert(30);

        List<Integer> result = new ArrayList<>();
        tree.accept(result::add);

        assertEquals(List.of(20, 10, 30), result);
    }

    @Test
    public void testLeftRightRotation() {
        tree.insert(30);
        tree.insert(10);
        tree.insert(20);

        List<Integer> result = new ArrayList<>();
        tree.accept(result::add);

        assertEquals(List.of(20, 10, 30), result);
    }

    @Test
    public void testRightLeftRotation() {
        tree.insert(10);
        tree.insert(30);
        tree.insert(20);

        List<Integer> result = new ArrayList<>();
        tree.accept(result::add);

        assertEquals(List.of(20, 10, 30), result);
    }

    @Test
    public void testWorksWithStrings() {
        Tree<String> stringTree = new AVLTree<>();
        stringTree.insert("banana");
        stringTree.insert("apple");
        stringTree.insert("cherry");

        List<String> result = new ArrayList<>();
        stringTree.accept(result::add);

        assertEquals(List.of("banana", "apple", "cherry"), result);
    }
}
