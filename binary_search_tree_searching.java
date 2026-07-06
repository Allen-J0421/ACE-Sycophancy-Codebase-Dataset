class Node {
    private int data;
    private Node left;
    private Node right;

    Node(int data) {
        this.data = data;
    }

    int getData() { return data; }
    Node getLeft() { return left; }
    Node getRight() { return right; }
    void setLeft(Node left) { this.left = left; }
    void setRight(Node right) { this.right = right; }
}

class BinarySearchTree {
    private Node root;

    void insert(int key) {
        root = insertRec(root, key);
    }

    private Node insertRec(Node node, int key) {
        if (node == null) return new Node(key);
        if (key < node.getData())
            node.setLeft(insertRec(node.getLeft(), key));
        else if (key > node.getData())
            node.setRight(insertRec(node.getRight(), key));
        return node;
    }

    boolean search(int key) {
        Node current = root;
        while (current != null) {
            if (current.getData() == key) return true;
            current = key > current.getData() ? current.getRight() : current.getLeft();
        }
        return false;
    }

    public static void main(String[] args) {
        BinarySearchTree bst = new BinarySearchTree();
        bst.insert(6);
        bst.insert(2);
        bst.insert(8);
        bst.insert(7);
        bst.insert(9);

        int key = 7;
        System.out.println(bst.search(key));
    }
}
