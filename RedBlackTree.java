import java.util.ArrayList;
import java.util.List;

public final class RedBlackTree {
    private enum Color {
        RED,
        BLACK
    }

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
        private Color color;

        private Node(int data, Color color) {
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

    private static final class InsertionState {
        private Rotation pendingRotation = Rotation.NONE;
    }

    private Node root;

    public void insert(int data) {
        if (root == null) {
            root = new Node(data, Color.BLACK);
            return;
        }

        InsertionState state = new InsertionState();
        root = insert(root, data, state);
        root.color = Color.BLACK;
        root.parent = null;
    }

    public void insertAll(int... values) {
        for (int value : values) {
            insert(value);
        }
    }

    public List<Integer> inorderValues() {
        List<Integer> values = new ArrayList<>();
        collectInorder(root, values);
        return values;
    }

    public String inorderString() {
        return RedBlackTreeFormatter.formatInorder(snapshot());
    }

    public void inorderTraversal() {
        System.out.print(inorderString());
    }

    public String formatTree() {
        return RedBlackTreeFormatter.formatTree(snapshot());
    }

    public void printTree() {
        System.out.print(formatTree());
    }

    public boolean isValidRedBlackTree() {
        return RedBlackTreeValidator.isValid(snapshot());
    }

    private Node insert(Node current, int data, InsertionState state) {
        if (current == null) {
            return new Node(data, Color.RED);
        }

        Side insertionSide = insertionSide(current, data);
        Node insertedChild = insert(current.child(insertionSide), data, state);
        current.setChild(insertionSide, insertedChild);
        insertedChild.parent = current;
        boolean redRedConflict = hasRedRedConflict(current, insertionSide);

        current = applyPendingRotation(current, state);

        if (redRedConflict) {
            resolveRedRedConflict(current, state);
        }

        return current;
    }

    private Node applyPendingRotation(Node current, InsertionState state) {
        switch (state.pendingRotation) {
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

        state.pendingRotation = Rotation.NONE;
        return current;
    }

    private void resolveRedRedConflict(Node current, InsertionState state) {
        Node parent = current.parent;
        Side side = sideOf(parent, current);
        Node uncle = siblingChild(parent, side);

        if (isBlack(uncle)) {
            state.pendingRotation = chooseRotation(current, side);
        } else {
            recolorForRedUncle(current, uncle);
        }
    }

    private void recolorAfterSingleRotation(Node newRoot, Side childSide) {
        newRoot.color = Color.BLACK;
        newRoot.child(childSide).color = Color.RED;
    }

    private void recolorForRedUncle(Node current, Node uncle) {
        uncle.color = Color.BLACK;
        current.color = Color.BLACK;
        if (current.parent != root) {
            current.parent.color = Color.RED;
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
        return node != null && node.color == Color.RED;
    }

    private boolean isBlack(Node node) {
        return node == null || node.color == Color.BLACK;
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

    private void collectInorder(Node node, List<Integer> values) {
        if (node == null) {
            return;
        }

        collectInorder(node.left, values);
        values.add(node.data);
        collectInorder(node.right, values);
    }

    private RedBlackTreeSnapshot.NodeSnapshot copyOf(Node node) {
        if (node == null) {
            return null;
        }

        return new RedBlackTreeSnapshot.NodeSnapshot(
            node.data,
            node.color == Color.BLACK,
            copyOf(node.left),
            copyOf(node.right)
        );
    }
}
