import java.util.List;

public final class RedBlackTree {
    private enum Rotation {
        NONE,
        LEFT_LEFT,
        RIGHT_RIGHT,
        LEFT_RIGHT,
        RIGHT_LEFT
    }

    private enum Side {
        LEFT,
        RIGHT
    }

    private static final class Node {
        private final int data;
        private Node left;
        private Node right;
        private Node parent;
        private RedBlackTreeColor color;

        private Node(int data, RedBlackTreeColor color) {
            this.data = data;
            this.color = color;
        }

        private Node child(Side side) {
            return side == Side.LEFT ? left : right;
        }

        private void setChild(Side side, Node child) {
            if (side == Side.LEFT) {
                left = child;
            } else {
                right = child;
            }
        }
    }

    private static final class InsertionResult {
        private final Node root;
        private final Rotation pendingRotation;

        private InsertionResult(Node root, Rotation pendingRotation) {
            this.root = root;
            this.pendingRotation = pendingRotation;
        }
    }

    private Node root;

    public void insert(int data) {
        if (root == null) {
            root = new Node(data, RedBlackTreeColor.BLACK);
            return;
        }

        root = insert(root, data).root;
        root.color = RedBlackTreeColor.BLACK;
        root.parent = null;
    }

    public void insertAll(int... values) {
        for (int value : values) {
            insert(value);
        }
    }

    public List<Integer> inorderValues() {
        return snapshot().inorderValues();
    }

    public String inorderString() {
        return snapshot().inorderString();
    }

    public void inorderTraversal() {
        System.out.print(inorderString());
    }

    public String formatTree() {
        return snapshot().formatTree();
    }

    public void printTree() {
        System.out.print(formatTree());
    }

    public boolean isValidRedBlackTree() {
        return snapshot().isValid();
    }

    private InsertionResult insert(Node current, int data) {
        if (current == null) {
            return new InsertionResult(new Node(data, RedBlackTreeColor.RED), Rotation.NONE);
        }

        Side insertionSide = insertionSide(current, data);
        InsertionResult childResult = insert(current.child(insertionSide), data);
        Node insertedChild = childResult.root;
        current.setChild(insertionSide, insertedChild);
        insertedChild.parent = current;
        boolean redRedConflict = hasRedRedConflict(current, insertionSide);

        current = applyPendingRotation(current, childResult.pendingRotation);
        Rotation pendingRotation = Rotation.NONE;

        if (redRedConflict) {
            pendingRotation = resolveRedRedConflict(current);
        }

        return new InsertionResult(current, pendingRotation);
    }

    private Node applyPendingRotation(Node current, Rotation pendingRotation) {
        switch (pendingRotation) {
            case LEFT_LEFT:
                current = rotateLeft(current);
                recolorAfterSingleRotation(current, Side.LEFT);
                break;
            case RIGHT_RIGHT:
                current = rotateRight(current);
                recolorAfterSingleRotation(current, Side.RIGHT);
                break;
            case RIGHT_LEFT:
                current = rotateChildThenCurrent(current, Side.RIGHT, Rotation.RIGHT_RIGHT);
                current = rotateLeft(current);
                recolorAfterSingleRotation(current, Side.LEFT);
                break;
            case LEFT_RIGHT:
                current = rotateChildThenCurrent(current, Side.LEFT, Rotation.LEFT_LEFT);
                current = rotateRight(current);
                recolorAfterSingleRotation(current, Side.RIGHT);
                break;
            case NONE:
                break;
        }

        return current;
    }

    private Rotation resolveRedRedConflict(Node current) {
        Node parent = current.parent;
        Side side = sideOf(parent, current);
        Node uncle = siblingChild(parent, side);

        if (isBlack(uncle)) {
            return chooseRotation(current, side);
        }

        recolorForRedUncle(current, uncle);
        return Rotation.NONE;
    }

    private void recolorAfterSingleRotation(Node newRoot, Side childSide) {
        newRoot.color = RedBlackTreeColor.BLACK;
        newRoot.child(childSide).color = RedBlackTreeColor.RED;
    }

    private void recolorForRedUncle(Node current, Node uncle) {
        uncle.color = RedBlackTreeColor.BLACK;
        current.color = RedBlackTreeColor.BLACK;
        if (current.parent != root) {
            current.parent.color = RedBlackTreeColor.RED;
        }
    }

    private Rotation chooseRotation(Node current, Side side) {
        if (side == Side.RIGHT) {
            if (isRed(current.child(Side.LEFT))) {
                return Rotation.RIGHT_LEFT;
            }
            if (isRed(current.child(Side.RIGHT))) {
                return Rotation.LEFT_LEFT;
            }
        } else {
            if (isRed(current.child(Side.LEFT))) {
                return Rotation.RIGHT_RIGHT;
            }
            if (isRed(current.child(Side.RIGHT))) {
                return Rotation.LEFT_RIGHT;
            }
        }

        return Rotation.NONE;
    }

    private Node rotateLeft(Node node) {
        Node pivot = node.right;
        Node transferredSubtree = pivot.left;

        pivot.left = node;
        pivot.parent = node.parent;
        node.parent = pivot;
        node.right = transferredSubtree;

        if (transferredSubtree != null) {
            transferredSubtree.parent = node;
        }

        return pivot;
    }

    private Node rotateRight(Node node) {
        Node pivot = node.left;
        Node transferredSubtree = pivot.right;

        pivot.right = node;
        pivot.parent = node.parent;
        node.parent = pivot;
        node.left = transferredSubtree;

        if (transferredSubtree != null) {
            transferredSubtree.parent = node;
        }

        return pivot;
    }

    private boolean isRed(Node node) {
        return node != null && node.color == RedBlackTreeColor.RED;
    }

    private boolean isBlack(Node node) {
        return node == null || node.color == RedBlackTreeColor.BLACK;
    }

    private Side insertionSide(Node current, int data) {
        return data < current.data ? Side.LEFT : Side.RIGHT;
    }

    private boolean hasRedRedConflict(Node current, Side childSide) {
        return current != root && isRed(current) && isRed(current.child(childSide));
    }

    private Side sideOf(Node parent, Node child) {
        return parent.right == child ? Side.RIGHT : Side.LEFT;
    }

    private Node siblingChild(Node parent, Side side) {
        return side == Side.RIGHT ? parent.left : parent.right;
    }

    private Node rotateChildThenCurrent(Node current, Side childSide, Rotation childRotation) {
        Node rotatedChild = childRotation == Rotation.RIGHT_RIGHT
            ? rotateRight(current.child(childSide))
            : rotateLeft(current.child(childSide));
        current.setChild(childSide, rotatedChild);
        rotatedChild.parent = current;
        return current;
    }

    RedBlackTreeSnapshot snapshot() {
        return new RedBlackTreeSnapshot(copyOf(root));
    }

    private RedBlackTreeSnapshot.NodeSnapshot copyOf(Node node) {
        if (node == null) {
            return null;
        }

        return new RedBlackTreeSnapshot.NodeSnapshot(
            node.data,
            node.color,
            copyOf(node.left),
            copyOf(node.right)
        );
    }
}
