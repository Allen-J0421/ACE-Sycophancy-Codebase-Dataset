class Node {
    int value;
    Node left;
    Node right;

    public Node(int value) {
        this.value = value;
        this.left = null;
        this.right = null;
    }
}

class BinarySearchTree {
    private Node root;

    public BinarySearchTree() {
        this.root = null;
    }

    public void insert(int value) {
        root = insertRecursive(root, value);
    }

    private Node insertRecursive(Node node, int value) {
        if (node == null) {
            return new Node(value);
        }

        if (value < node.value) {
            node.left = insertRecursive(node.left, value);
        } else if (value > node.value) {
            node.right = insertRecursive(node.right, value);
        }
        return node;
    }

    public boolean search(int key) {
        return searchRecursive(root, key);
    }

    private boolean searchRecursive(Node node, int key) {
        if (node == null) {
            return false;
        }

        if (key == node.value) {
            return true;
        } else if (key < node.value) {
            return searchRecursive(node.left, key);
        } else {
            return searchRecursive(node.right, key);
        }
    }

    public Node getRoot() {
        return root;
    }
}

class BinarySearchTreeDemo {
    public static void main(String[] args) {
        BinarySearchTree bst = new BinarySearchTree();

        bst.insert(6);
        bst.insert(2);
        bst.insert(8);
        bst.insert(7);
        bst.insert(9);

        int searchKey = 7;
        boolean found = bst.search(searchKey);

        System.out.println("Search for " + searchKey + ": " + found);
    }
}
