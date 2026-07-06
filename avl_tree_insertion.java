import java.util.*;

interface TreeVisitor {
    void visit(int key);
}

interface Tree {
    void insert(int key);
    void accept(TreeVisitor visitor);
}

class AVLTree implements Tree {

    private Node root;

    private static class Node {
        int key;
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

    private static class AVLBalancer {

        static int height(Node n) {
            return n == null ? 0 : n.height;
        }

        static int getBalance(Node n) {
            return n == null ? 0 : height(n.left) - height(n.right);
        }

        static void updateHeight(Node n) {
            n.height = 1 + Math.max(height(n.left), height(n.right));
        }

        static Node rightRotate(Node y) {
            Node x = y.left;
            Node T2 = x.right;

            x.right = y;
            y.left = T2;

            updateHeight(y);
            updateHeight(x);

            return x;
        }

        static Node leftRotate(Node x) {
            Node y = x.right;
            Node T2 = y.left;

            y.left = x;
            x.right = T2;

            updateHeight(x);
            updateHeight(y);

            return y;
        }

        static Node rebalance(Node node, int key) {
            int balance = getBalance(node);

            if (balance > 1 && key < node.left.key)
                return rightRotate(node);

            if (balance < -1 && key > node.right.key)
                return leftRotate(node);

            if (balance > 1 && key > node.left.key) {
                node.left = leftRotate(node.left);
                return rightRotate(node);
            }

            if (balance < -1 && key < node.right.key) {
                node.right = rightRotate(node.right);
                return leftRotate(node);
            }

            return node;
        }
    }

    public void insert(int key) {
        root = insertNode(root, key);
    }

    private Node insertNode(Node node, int key) {
        if (node == null)
            return new Node(key);

        if (key < node.key)
            node.left = insertNode(node.left, key);
        else if (key > node.key)
            node.right = insertNode(node.right, key);
        else
            return node;

        AVLBalancer.updateHeight(node);
        return AVLBalancer.rebalance(node, key);
    }

    public void accept(TreeVisitor visitor) {
        preOrderTraversal(root, visitor);
    }

    private void preOrderTraversal(Node node, TreeVisitor visitor) {
        if (node != null) {
            visitor.visit(node.key);
            preOrderTraversal(node.left, visitor);
            preOrderTraversal(node.right, visitor);
        }
    }

    public static void main(String[] args) {
        Tree tree = new AVLTree();

        tree.insert(10);
        tree.insert(20);
        tree.insert(30);
        tree.insert(40);
        tree.insert(50);
        tree.insert(25);

        tree.accept(key -> System.out.print(key + " "));
    }
}
