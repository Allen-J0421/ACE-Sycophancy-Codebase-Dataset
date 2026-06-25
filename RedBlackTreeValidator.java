final class RedBlackTreeValidator {
    private RedBlackTreeValidator() {
    }

    static boolean isValid(RedBlackTreeSnapshot snapshot) {
        RedBlackTreeSnapshot.NodeSnapshot root = snapshot.root();
        if (root == null) {
            return true;
        }

        if (root.color() != RedBlackTreeColor.BLACK) {
            return false;
        }

        return isBinarySearchTree(root, Long.MIN_VALUE, Long.MAX_VALUE)
            && hasNoRedRedViolation(root)
            && blackHeight(root) != -1;
    }

    private static boolean isBinarySearchTree(
        RedBlackTreeSnapshot.NodeSnapshot node,
        long minInclusive,
        long maxExclusive
    ) {
        if (node == null) {
            return true;
        }

        if (node.data() < minInclusive || node.data() >= maxExclusive) {
            return false;
        }

        return isBinarySearchTree(node.left(), minInclusive, node.data())
            && isBinarySearchTree(node.right(), node.data(), maxExclusive);
    }

    private static boolean hasNoRedRedViolation(RedBlackTreeSnapshot.NodeSnapshot node) {
        if (node == null) {
            return true;
        }

        if (isRed(node) && (isRed(node.left()) || isRed(node.right()))) {
            return false;
        }

        return hasNoRedRedViolation(node.left()) && hasNoRedRedViolation(node.right());
    }

    private static int blackHeight(RedBlackTreeSnapshot.NodeSnapshot node) {
        if (node == null) {
            return 1;
        }

        int leftHeight = blackHeight(node.left());
        int rightHeight = blackHeight(node.right());
        if (leftHeight == -1 || rightHeight == -1 || leftHeight != rightHeight) {
            return -1;
        }

        return leftHeight + (node.color() == RedBlackTreeColor.BLACK ? 1 : 0);
    }

    private static boolean isRed(RedBlackTreeSnapshot.NodeSnapshot node) {
        return node != null && node.color() == RedBlackTreeColor.RED;
    }
}
