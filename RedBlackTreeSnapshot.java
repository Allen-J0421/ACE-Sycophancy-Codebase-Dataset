import java.util.ArrayList;
import java.util.List;

final class RedBlackTreeSnapshot {
    record NodeSnapshot(
        int data,
        RedBlackTreeColor color,
        NodeSnapshot left,
        NodeSnapshot right
    ) {
    }

    private final NodeSnapshot root;

    RedBlackTreeSnapshot(NodeSnapshot root) {
        this.root = root;
    }

    NodeSnapshot root() {
        return root;
    }

    List<Integer> inorderValues() {
        List<Integer> values = new ArrayList<>();
        collectInorder(root, values);
        return values;
    }

    private void collectInorder(NodeSnapshot node, List<Integer> values) {
        if (node == null) {
            return;
        }

        collectInorder(node.left(), values);
        values.add(node.data());
        collectInorder(node.right(), values);
    }
}
