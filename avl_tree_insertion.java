import java.util.*;

interface TreeVisitor<T> {
    void visit(T key);
}

interface Tree<T extends Comparable<T>> {
    void insert(T key);
    void accept(TreeVisitor<T> visitor);
}

class AVLTree<T extends Comparable<T>> implements Tree<T> {

    private Node<T> root;

    private static class Node<T extends Comparable<T>> {
        T key;
        Node<T> left;
        Node<T> right;
        int height;

        Node(T k) {
            key = k;
            left = null;
            right = null;
            height = 1;
        }
    }

    private static class AVLBalancer {

        static <T extends Comparable<T>> int height(Node<T> n) {
            return n == null ? 0 : n.height;
        }

        static <T extends Comparable<T>> int getBalance(Node<T> n) {
            return n == null ? 0 : height(n.left) - height(n.right);
        }

        static <T extends Comparable<T>> void updateHeight(Node<T> n) {
            n.height = 1 + Math.max(height(n.left), height(n.right));
        }

        static <T extends Comparable<T>> Node<T> rightRotate(Node<T> y) {
            Node<T> x = y.left;
            Node<T> subtree = x.right;

            x.right = y;
            y.left = subtree;

            updateHeight(y);
            updateHeight(x);

            return x;
        }

        static <T extends Comparable<T>> Node<T> leftRotate(Node<T> x) {
            Node<T> y = x.right;
            Node<T> subtree = y.left;

            y.left = x;
            x.right = subtree;

            updateHeight(x);
            updateHeight(y);

            return y;
        }

        static <T extends Comparable<T>> Node<T> rebalance(Node<T> node, T key) {
            int balance = getBalance(node);

            if (balance > 1 && key.compareTo(node.left.key) < 0)
                return rightRotate(node);

            if (balance < -1 && key.compareTo(node.right.key) > 0)
                return leftRotate(node);

            if (balance > 1 && key.compareTo(node.left.key) > 0) {
                node.left = leftRotate(node.left);
                return rightRotate(node);
            }

            if (balance < -1 && key.compareTo(node.right.key) < 0) {
                node.right = rightRotate(node.right);
                return leftRotate(node);
            }

            return node;
        }
    }

    private static class TreeTraverser {

        static <T extends Comparable<T>> Node<T> insert(Node<T> node, T key) {
            if (node == null)
                return new Node<>(key);

            if (key.compareTo(node.key) < 0)
                node.left = insert(node.left, key);
            else if (key.compareTo(node.key) > 0)
                node.right = insert(node.right, key);
            else
                return node;

            AVLBalancer.updateHeight(node);
            return AVLBalancer.rebalance(node, key);
        }

        static <T extends Comparable<T>> void traverse(Node<T> node, TreeVisitor<T> visitor) {
            if (node != null) {
                visitor.visit(node.key);
                traverse(node.left, visitor);
                traverse(node.right, visitor);
            }
        }
    }

    public void insert(T key) {
        root = TreeTraverser.insert(root, key);
    }

    public void accept(TreeVisitor<T> visitor) {
        TreeTraverser.traverse(root, visitor);
    }

    public static void main(String[] args) {
        Tree<Integer> tree = new AVLTree<>();

        tree.insert(10);
        tree.insert(20);
        tree.insert(30);
        tree.insert(40);
        tree.insert(50);
        tree.insert(25);

        tree.accept(key -> System.out.print(key + " "));
    }
}
