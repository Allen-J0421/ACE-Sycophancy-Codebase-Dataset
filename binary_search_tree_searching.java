final class BinarySearchTree {
    private static final class Node {
        final int data;
        Node left;
        Node right;

        private Node(int item) {
            this.data = item;
        }
    }

    private static final class SearchResult {
        final Node parent;
        final Node node;
        final boolean wentLeft;

        private SearchResult(Node parent, Node node, boolean wentLeft) {
            this.parent = parent;
            this.node = node;
            this.wentLeft = wentLeft;
        }
    }

    private Node root;

    BinarySearchTree() {
    }

    boolean contains(int key) {
        return find(key).node != null;
    }

    void insert(int key) {
        if (root == null) {
            root = new Node(key);
            return;
        }

        SearchResult result = find(key);
        if (result.node != null) {
            return;
        }

        if (result.wentLeft) {
            result.parent.left = new Node(key);
        } else {
            result.parent.right = new Node(key);
        }
    }

    private SearchResult find(int key) {
        Node parent = null;
        Node current = root;
        boolean wentLeft = false;

        while (current != null) {
            if (key == current.data) {
                return new SearchResult(parent, current, wentLeft);
            }

            parent = current;
            if (key < current.data) {
                current = current.left;
                wentLeft = true;
            } else {
                current = current.right;
                wentLeft = false;
            }
        }

        return new SearchResult(parent, null, wentLeft);
    }

    public static void main(String[] args) {
        BinarySearchTree tree = new BinarySearchTree();
        tree.insert(6);
        tree.insert(2);
        tree.insert(8);
        tree.insert(7);
        tree.insert(9);
        System.out.println(tree.contains(7));
    }
}
