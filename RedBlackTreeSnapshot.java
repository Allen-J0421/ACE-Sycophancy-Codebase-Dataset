final class RedBlackTreeSnapshot {
    record NodeSnapshot(int data, boolean black, NodeSnapshot left, NodeSnapshot right) {
    }

    private final NodeSnapshot root;

    RedBlackTreeSnapshot(NodeSnapshot root) {
        this.root = root;
    }

    NodeSnapshot root() {
        return root;
    }
}
