import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class RedBlackTreeTest {
    @Test
    void insertProducesSortedInorderTraversal() {
        RedBlackTree tree = new RedBlackTree();
        tree.insertAll(1, 4, 6, 3, 5, 7, 8, 2, 9);

        assertEquals(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9), tree.inorderValues());
        assertEquals("1 2 3 4 5 6 7 8 9", tree.inorderString());
        assertTrue(tree.isValidRedBlackTree());
    }

    @Test
    void descendingInsertionsRemainOrderedAndValid() {
        RedBlackTree tree = new RedBlackTree();
        tree.insertAll(9, 8, 7, 6, 5, 4, 3, 2, 1);

        assertEquals(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9), tree.inorderValues());
        assertTrue(tree.isValidRedBlackTree());
    }

    @Test
    void ascendingInsertionsPreserveRedBlackInvariants() {
        RedBlackTree tree = new RedBlackTree();

        for (int value = 1; value <= 100; value++) {
            tree.insert(value);
        }

        assertEquals(100, tree.inorderValues().size());
        assertTrue(tree.isValidRedBlackTree());
    }

    @Test
    void snapshotUtilitiesRemainConsistent() {
        RedBlackTree tree = new RedBlackTree();
        tree.insertAll(10, 5, 15, 3, 8, 12, 18);

        RedBlackTreeSnapshot snapshot = tree.snapshot();

        assertEquals("3 5 8 10 12 15 18", RedBlackTreeFormatter.formatInorder(snapshot));
        assertTrue(RedBlackTreeValidator.isValid(snapshot));
    }
}
