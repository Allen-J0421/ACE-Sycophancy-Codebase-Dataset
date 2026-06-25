class Node {
    final int key;
    Node left;
    Node right;
    int height;

    Node(int k) {
        key = k;
        left = null;
        right = null;
        height = 1;
    }
}

class AVLTree {

    static int height(Node node) {
        if (node == null) {
            return 0;
        }
        return node.height;
    }

    static void updateHeight(Node node) {
        node.height = 1 + Math.max(height(node.left), height(node.right));
    }

    static Node rightRotate(Node y) {
        Node x = y.left;
        Node subtree = x.right;

        x.right = y;
        y.left = subtree;

        updateHeight(y);
        updateHeight(x);

        return x;
    }

    static Node leftRotate(Node x) {
        Node y = x.right;
        Node subtree = y.left;

        y.left = x;
        x.right = subtree;

        updateHeight(x);
        updateHeight(y);

        return y;
    }

    static int getBalance(Node node) {
        if (node == null) {
            return 0;
        }
        return height(node.left) - height(node.right);
    }

    static Node rebalance(Node node, int key) {
        int balance = getBalance(node);

        if (balance > 1) {
            if (key < node.left.key) {
                return rightRotate(node);
            }

            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        if (balance < -1) {
            if (key > node.right.key) {
                return leftRotate(node);
            }

            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return node;
    }

    static Node insert(Node node, int key) {
        if (node == null) {
            return new Node(key);
        }

        if (key < node.key) {
            node.left = insert(node.left, key);
        } else if (key > node.key) {
            node.right = insert(node.right, key);
        } else {
            return node;
        }

        updateHeight(node);
        return rebalance(node, key);
    }

    static void preOrder(Node root) {
        if (root != null) {
            System.out.print(root.key + " ");
            preOrder(root.left);
            preOrder(root.right);
        }
    }

    public static void main(String[] args) {
        Node root = null;

        root = insert(root, 10);
        root = insert(root, 20);
        root = insert(root, 30);
        root = insert(root, 40);
        root = insert(root, 50);
        root = insert(root, 25);

        preOrder(root);
    }
}
