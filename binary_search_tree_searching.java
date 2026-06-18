import java.util.*;

class Node<T extends Comparable<T>> {
    T value;
    Node<T> left;
    Node<T> right;

    public Node(T value) {
        this.value = value;
    }
}

interface TreeTraversal<T> {
    List<T> traverse();
}

class InOrderTraversal<T extends Comparable<T>> implements TreeTraversal<T> {
    private Node<T> root;

    public InOrderTraversal(Node<T> root) {
        this.root = root;
    }

    @Override
    public List<T> traverse() {
        List<T> result = new ArrayList<>();
        inOrderRecursive(root, result);
        return result;
    }

    private void inOrderRecursive(Node<T> node, List<T> result) {
        if (node == null) return;
        inOrderRecursive(node.left, result);
        result.add(node.value);
        inOrderRecursive(node.right, result);
    }
}

class PreOrderTraversal<T extends Comparable<T>> implements TreeTraversal<T> {
    private Node<T> root;

    public PreOrderTraversal(Node<T> root) {
        this.root = root;
    }

    @Override
    public List<T> traverse() {
        List<T> result = new ArrayList<>();
        preOrderRecursive(root, result);
        return result;
    }

    private void preOrderRecursive(Node<T> node, List<T> result) {
        if (node == null) return;
        result.add(node.value);
        preOrderRecursive(node.left, result);
        preOrderRecursive(node.right, result);
    }
}

class PostOrderTraversal<T extends Comparable<T>> implements TreeTraversal<T> {
    private Node<T> root;

    public PostOrderTraversal(Node<T> root) {
        this.root = root;
    }

    @Override
    public List<T> traverse() {
        List<T> result = new ArrayList<>();
        postOrderRecursive(root, result);
        return result;
    }

    private void postOrderRecursive(Node<T> node, List<T> result) {
        if (node == null) return;
        postOrderRecursive(node.left, result);
        postOrderRecursive(node.right, result);
        result.add(node.value);
    }
}

class LevelOrderTraversal<T extends Comparable<T>> implements TreeTraversal<T> {
    private Node<T> root;

    public LevelOrderTraversal(Node<T> root) {
        this.root = root;
    }

    @Override
    public List<T> traverse() {
        List<T> result = new ArrayList<>();
        if (root == null) return result;

        Queue<Node<T>> queue = new LinkedList<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            Node<T> node = queue.poll();
            result.add(node.value);

            if (node.left != null) queue.add(node.left);
            if (node.right != null) queue.add(node.right);
        }
        return result;
    }
}

class TreeValidator<T extends Comparable<T>> {
    public boolean isValidBST(Node<T> root) {
        return isValidBSTRecursive(root, null, null);
    }

    private boolean isValidBSTRecursive(Node<T> node, T min, T max) {
        if (node == null) return true;

        if (min != null && node.value.compareTo(min) <= 0) return false;
        if (max != null && node.value.compareTo(max) >= 0) return false;

        return isValidBSTRecursive(node.left, min, node.value) &&
               isValidBSTRecursive(node.right, node.value, max);
    }
}

class TreeAnalyzer<T extends Comparable<T>> {
    private Node<T> root;

    public TreeAnalyzer(Node<T> root) {
        this.root = root;
    }

    public int getHeight() {
        return getHeightRecursive(root);
    }

    private int getHeightRecursive(Node<T> node) {
        return node == null ? -1 : 1 + Math.max(getHeightRecursive(node.left),
                                                 getHeightRecursive(node.right));
    }

    public int getSize() {
        return getSizeRecursive(root);
    }

    private int getSizeRecursive(Node<T> node) {
        return node == null ? 0 : 1 + getSizeRecursive(node.left) + getSizeRecursive(node.right);
    }

    public boolean isBalanced() {
        return isBalancedRecursive(root).isBalanced;
    }

    private BalanceInfo isBalancedRecursive(Node<T> node) {
        if (node == null) return new BalanceInfo(true, -1);

        BalanceInfo leftInfo = isBalancedRecursive(node.left);
        if (!leftInfo.isBalanced) return new BalanceInfo(false, 0);

        BalanceInfo rightInfo = isBalancedRecursive(node.right);
        if (!rightInfo.isBalanced) return new BalanceInfo(false, 0);

        int heightDiff = Math.abs(leftInfo.height - rightInfo.height);
        boolean balanced = heightDiff <= 1;
        int height = 1 + Math.max(leftInfo.height, rightInfo.height);

        return new BalanceInfo(balanced, height);
    }

    private static class BalanceInfo {
        boolean isBalanced;
        int height;

        BalanceInfo(boolean isBalanced, int height) {
            this.isBalanced = isBalanced;
            this.height = height;
        }
    }

    public double getBalanceFactor() {
        BalanceFactor bf = getBalanceFactorRecursive(root);
        return bf.totalDiff > 0 ? (double) bf.balancedCount / bf.totalDiff : 1.0;
    }

    private BalanceFactor getBalanceFactorRecursive(Node<T> node) {
        if (node == null) return new BalanceFactor(0, 0);

        BalanceFactor left = getBalanceFactorRecursive(node.left);
        BalanceFactor right = getBalanceFactorRecursive(node.right);

        int diff = Math.abs(left.totalDiff - right.totalDiff);
        int balanced = left.balancedCount + right.balancedCount + (diff <= 1 ? 1 : 0);

        return new BalanceFactor(diff + left.totalDiff + right.totalDiff, balanced);
    }

    private static class BalanceFactor {
        int totalDiff;
        int balancedCount;

        BalanceFactor(int totalDiff, int balancedCount) {
            this.totalDiff = totalDiff;
            this.balancedCount = balancedCount;
        }
    }
}

interface TreeOperations<T extends Comparable<T>> {
    void insert(T value);
    boolean search(T key);
    boolean delete(T key);
    T getMin();
    T getMax();
    Optional<T> getFloor(T key);
    Optional<T> getCeiling(T key);
    int getRank(T key);
    List<T> getRange(T min, T max);
    int size();
    boolean isEmpty();
}

class BinarySearchTree<T extends Comparable<T>> implements TreeOperations<T> {
    private Node<T> root;
    private int size;

    public BinarySearchTree() {
        this.root = null;
        this.size = 0;
    }

    @Override
    public void insert(T value) {
        if (value == null) throw new IllegalArgumentException("Value cannot be null");
        root = insertRecursive(root, value);
    }

    private Node<T> insertRecursive(Node<T> node, T value) {
        if (node == null) {
            size++;
            return new Node<>(value);
        }

        int comparison = value.compareTo(node.value);
        if (comparison < 0) {
            node.left = insertRecursive(node.left, value);
        } else if (comparison > 0) {
            node.right = insertRecursive(node.right, value);
        }
        return node;
    }

    @Override
    public boolean search(T key) {
        if (key == null) throw new IllegalArgumentException("Key cannot be null");
        return searchRecursive(root, key);
    }

    private boolean searchRecursive(Node<T> node, T key) {
        if (node == null) return false;

        int comparison = key.compareTo(node.value);
        if (comparison == 0) return true;
        else if (comparison < 0) return searchRecursive(node.left, key);
        else return searchRecursive(node.right, key);
    }

    @Override
    public boolean delete(T key) {
        if (key == null) throw new IllegalArgumentException("Key cannot be null");
        int oldSize = size;
        root = deleteRecursive(root, key);
        return size < oldSize;
    }

    private Node<T> deleteRecursive(Node<T> node, T key) {
        if (node == null) return null;

        int comparison = key.compareTo(node.value);
        if (comparison < 0) {
            node.left = deleteRecursive(node.left, key);
        } else if (comparison > 0) {
            node.right = deleteRecursive(node.right, key);
        } else {
            size--;
            if (node.left == null) return node.right;
            if (node.right == null) return node.left;

            Node<T> minRight = findMinNode(node.right);
            node.value = minRight.value;
            node.right = deleteRecursive(node.right, minRight.value);
            size++;
        }
        return node;
    }

    @Override
    public T getMin() {
        if (isEmpty()) throw new IllegalStateException("Tree is empty");
        return findMinNode(root).value;
    }

    @Override
    public T getMax() {
        if (isEmpty()) throw new IllegalStateException("Tree is empty");
        return findMaxNode(root).value;
    }

    @Override
    public Optional<T> getFloor(T key) {
        if (key == null) throw new IllegalArgumentException("Key cannot be null");
        return Optional.ofNullable(getFloorRecursive(root, key));
    }

    private T getFloorRecursive(Node<T> node, T key) {
        if (node == null) return null;

        if (key.compareTo(node.value) == 0) return node.value;

        if (key.compareTo(node.value) < 0) {
            return getFloorRecursive(node.left, key);
        }

        T floor = getFloorRecursive(node.right, key);
        return floor != null ? floor : node.value;
    }

    @Override
    public Optional<T> getCeiling(T key) {
        if (key == null) throw new IllegalArgumentException("Key cannot be null");
        return Optional.ofNullable(getCeilingRecursive(root, key));
    }

    private T getCeilingRecursive(Node<T> node, T key) {
        if (node == null) return null;

        if (key.compareTo(node.value) == 0) return node.value;

        if (key.compareTo(node.value) > 0) {
            return getCeilingRecursive(node.right, key);
        }

        T ceiling = getCeilingRecursive(node.left, key);
        return ceiling != null ? ceiling : node.value;
    }

    @Override
    public int getRank(T key) {
        if (key == null) throw new IllegalArgumentException("Key cannot be null");
        return getRankRecursive(root, key);
    }

    private int getRankRecursive(Node<T> node, T key) {
        if (node == null) return 0;

        int comparison = key.compareTo(node.value);
        if (comparison < 0) {
            return getRankRecursive(node.left, key);
        } else if (comparison > 0) {
            return 1 + getSizeRecursive(node.left) + getRankRecursive(node.right, key);
        } else {
            return getSizeRecursive(node.left);
        }
    }

    @Override
    public List<T> getRange(T min, T max) {
        if (min == null || max == null) throw new IllegalArgumentException("Range bounds cannot be null");
        if (min.compareTo(max) > 0) throw new IllegalArgumentException("Min cannot be greater than max");

        List<T> result = new ArrayList<>();
        getRangeRecursive(root, min, max, result);
        return result;
    }

    private void getRangeRecursive(Node<T> node, T min, T max, List<T> result) {
        if (node == null) return;

        int cmpMin = node.value.compareTo(min);
        int cmpMax = node.value.compareTo(max);

        if (cmpMin >= 0) getRangeRecursive(node.left, min, max, result);
        if (cmpMin >= 0 && cmpMax <= 0) result.add(node.value);
        if (cmpMax <= 0) getRangeRecursive(node.right, min, max, result);
    }

    private Node<T> findMinNode(Node<T> node) {
        while (node.left != null) node = node.left;
        return node;
    }

    private Node<T> findMaxNode(Node<T> node) {
        while (node.right != null) node = node.right;
        return node;
    }

    private int getSizeRecursive(Node<T> node) {
        return node == null ? 0 : 1 + getSizeRecursive(node.left) + getSizeRecursive(node.right);
    }

    public Node<T> getRoot() {
        return root;
    }

    public List<T> traverse(TreeTraversal<T> traversal) {
        return traversal.traverse();
    }

    public TreeValidator<T> getValidator() {
        return new TreeValidator<>();
    }

    public TreeAnalyzer<T> getAnalyzer() {
        return new TreeAnalyzer<>(root);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public String toString() {
        return "BST(" + traverse(new InOrderTraversal<>(root)) + ")";
    }
}

class BinarySearchTreeDemo {
    public static void main(String[] args) {
        BinarySearchTree<Integer> bst = new BinarySearchTree<>();

        System.out.println("=== Building Tree ===");
        int[] values = {6, 2, 8, 7, 9, 1, 4, 3, 5};
        for (int val : values) {
            bst.insert(val);
        }
        System.out.println("Tree: " + bst);

        System.out.println("\n=== Tree Analysis ===");
        TreeAnalyzer<Integer> analyzer = bst.getAnalyzer();
        System.out.println("Size: " + bst.size());
        System.out.println("Height: " + analyzer.getHeight());
        System.out.println("Is Balanced: " + analyzer.isBalanced());
        System.out.println("Min: " + bst.getMin() + ", Max: " + bst.getMax());

        System.out.println("\n=== Traversals ===");
        System.out.println("In-order: " + bst.traverse(new InOrderTraversal<>(bst.getRoot())));
        System.out.println("Pre-order: " + bst.traverse(new PreOrderTraversal<>(bst.getRoot())));
        System.out.println("Post-order: " + bst.traverse(new PostOrderTraversal<>(bst.getRoot())));
        System.out.println("Level-order: " + bst.traverse(new LevelOrderTraversal<>(bst.getRoot())));

        System.out.println("\n=== Floor & Ceiling ===");
        int[] queries = {3, 5, 7, 10};
        for (int q : queries) {
            System.out.println("Floor(" + q + "): " + bst.getFloor(q) +
                             ", Ceiling(" + q + "): " + bst.getCeiling(q));
        }

        System.out.println("\n=== Rank Queries ===");
        for (int q : new int[]{1, 4, 8}) {
            System.out.println("Rank of " + q + ": " + bst.getRank(q));
        }

        System.out.println("\n=== Range Queries ===");
        List<Integer> range1 = bst.getRange(3, 7);
        List<Integer> range2 = bst.getRange(1, 5);
        System.out.println("Range [3, 7]: " + range1);
        System.out.println("Range [1, 5]: " + range2);

        System.out.println("\n=== Validation ===");
        TreeValidator<Integer> validator = bst.getValidator();
        System.out.println("Is valid BST: " + validator.isValidBST(bst.getRoot()));

        System.out.println("\n=== Delete Operations ===");
        bst.delete(1);
        bst.delete(6);
        System.out.println("After deleting 1 and 6: " + bst);
        System.out.println("Size: " + bst.size());

        System.out.println("\n=== String BST ===");
        BinarySearchTree<String> stringBst = new BinarySearchTree<>();
        String[] words = {"dog", "cat", "zebra", "apple", "monkey", "banana"};
        for (String word : words) stringBst.insert(word);
        System.out.println("Sorted: " + stringBst);
        System.out.println("Floor of 'fox': " + stringBst.getFloor("fox"));
        System.out.println("Ceiling of 'fox': " + stringBst.getCeiling("fox"));
    }
}
