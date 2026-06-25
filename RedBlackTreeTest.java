import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class RedBlackTreeTest {
    @Test
    void insertProducesSortedInorderTraversal() {
        RedBlackTree tree = new RedBlackTree();
        int[] values = {1, 4, 6, 3, 5, 7, 8, 2, 9};

        for (int value : values) {
            tree.insert(value);
        }

        assertEquals(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9), tree.inorderValues());
        assertTrue(tree.isValidRedBlackTree());
    }

    @Test
    void descendingInsertionsRemainOrderedAndValid() {
        RedBlackTree tree = new RedBlackTree();
        int[] values = {9, 8, 7, 6, 5, 4, 3, 2, 1};

        for (int value : values) {
            tree.insert(value);
        }

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
}
