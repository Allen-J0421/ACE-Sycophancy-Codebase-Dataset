import java.util.*;
import java.io.Serializable;

class Node<T extends Comparable<T>> {
    T value;
    Node<T> left;
    Node<T> right;

    public Node(T value) {
        this.value = value;
    }
}

interface NodeVisitor<T extends Comparable<T>, R> {
    R visit(Node<T> node);
}

class TreeMetrics {
    public final int height;
    public final int size;
    public final double balanceFactor;
    public final boolean isBalanced;
    public final int minDepth;
    public final int maxDepth;

    public TreeMetrics(int height, int size, double balanceFactor, boolean isBalanced,
                      int minDepth, int maxDepth) {
        this.height = height;
        this.size = size;
        this.balanceFactor = balanceFactor;
        this.isBalanced = isBalanced;
        this.minDepth = minDepth;
        this.maxDepth = maxDepth;
    }

    @Override
    public String toString() {
        return String.format("TreeMetrics{height=%d, size=%d, balance=%.2f, balanced=%s, depths=[%d,%d]}",
                           height, size, balanceFactor, isBalanced, minDepth, maxDepth);
    }
}

interface TreeTraversal<T> {
    List<T> traverse();
}

abstract class AbstractTraversal<T extends Comparable<T>> implements TreeTraversal<T> {
    protected Node<T> root;

    public AbstractTraversal(Node<T> root) {
        this.root = root;
    }

    protected void processNode(Node<T> node, List<T> result) {
        if (node != null) result.add(node.value);
    }
}

class InOrderTraversal<T extends Comparable<T>> extends AbstractTraversal<T> {
    public InOrderTraversal(Node<T> root) {
        super(root);
    }

    @Override
    public List<T> traverse() {
        List<T> result = new ArrayList<>();
        traverseRecursive(root, result);
        return result;
    }

    private void traverseRecursive(Node<T> node, List<T> result) {
        if (node == null) return;
        traverseRecursive(node.left, result);
        processNode(node, result);
        traverseRecursive(node.right, result);
    }
}

class PreOrderTraversal<T extends Comparable<T>> extends AbstractTraversal<T> {
    public PreOrderTraversal(Node<T> root) {
        super(root);
    }

    @Override
    public List<T> traverse() {
        List<T> result = new ArrayList<>();
        traverseRecursive(root, result);
        return result;
    }

    private void traverseRecursive(Node<T> node, List<T> result) {
        if (node == null) return;
        processNode(node, result);
        traverseRecursive(node.left, result);
        traverseRecursive(node.right, result);
    }
}

class PostOrderTraversal<T extends Comparable<T>> extends AbstractTraversal<T> {
    public PostOrderTraversal(Node<T> root) {
        super(root);
    }

    @Override
    public List<T> traverse() {
        List<T> result = new ArrayList<>();
        traverseRecursive(root, result);
        return result;
    }

    private void traverseRecursive(Node<T> node, List<T> result) {
        if (node == null) return;
        traverseRecursive(node.left, result);
        traverseRecursive(node.right, result);
        processNode(node, result);
    }
}

class LevelOrderTraversal<T extends Comparable<T>> extends AbstractTraversal<T> {
    public LevelOrderTraversal(Node<T> root) {
        super(root);
    }

    @Override
    public List<T> traverse() {
        List<T> result = new ArrayList<>();
        if (root == null) return result;

        Queue<Node<T>> queue = new LinkedList<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            Node<T> node = queue.poll();
            processNode(node, result);

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

    public String getValidationReport(Node<T> root) {
        boolean valid = isValidBST(root);
        return String.format("BST Validation: %s", valid ? "VALID" : "INVALID");
    }
}

class TreeAnalyzer<T extends Comparable<T>> {
    private Node<T> root;
    private Map<String, Object> cache;

    public TreeAnalyzer(Node<T> root) {
        this.root = root;
        this.cache = new HashMap<>();
    }

    public TreeMetrics getMetrics() {
        int height = getHeight();
        int size = getSize();
        DepthInfo depthInfo = getDepthInfo();
        double balanceFactor = calculateBalanceFactor();
        boolean isBalanced = isBalanced();

        return new TreeMetrics(height, size, balanceFactor, isBalanced,
                              depthInfo.minDepth, depthInfo.maxDepth);
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

    private DepthInfo getDepthInfo() {
        DepthInfo info = new DepthInfo();
        calculateDepthRecursive(root, 0, info);
        return info;
    }

    private void calculateDepthRecursive(Node<T> node, int depth, DepthInfo info) {
        if (node == null) return;

        if (node.left == null && node.right == null) {
            info.minDepth = Math.min(info.minDepth == Integer.MAX_VALUE ? depth : info.minDepth, depth);
            info.maxDepth = Math.max(info.maxDepth, depth);
        }

        calculateDepthRecursive(node.left, depth + 1, info);
        calculateDepthRecursive(node.right, depth + 1, info);
    }

    private double calculateBalanceFactor() {
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

    private static class BalanceInfo {
        boolean isBalanced;
        int height;

        BalanceInfo(boolean isBalanced, int height) {
            this.isBalanced = isBalanced;
            this.height = height;
        }
    }

    private static class DepthInfo {
        int minDepth = Integer.MAX_VALUE;
        int maxDepth = 0;
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
    Optional<T> getSuccessor(T key);
    Optional<T> getPredecessor(T key);
    int getRank(T key);
    List<T> getRange(T min, T max);
    int countInRange(T min, T max);
    boolean containsAll(T... values);
    int size();
    boolean isEmpty();
}

class BinarySearchTree<T extends Comparable<T>> implements TreeOperations<T>, Iterable<T> {
    private Node<T> root;
    private int size;

    public BinarySearchTree() {
        this.root = null;
        this.size = 0;
    }

    public static <T extends Comparable<T>> BSTBuilder<T> builder() {
        return new BSTBuilder<>();
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
    public Optional<T> getSuccessor(T key) {
        Optional<T> ceiling = getCeiling(key);
        if (!ceiling.isPresent()) return Optional.empty();

        T ceil = ceiling.get();
        if (ceil.compareTo(key) == 0) {
            return Optional.ofNullable(getSuccessorRecursive(root, key));
        }
        return Optional.of(ceil);
    }

    private T getSuccessorRecursive(Node<T> node, T key) {
        if (node == null) return null;

        if (key.compareTo(node.value) < 0) {
            T left = getSuccessorRecursive(node.left, key);
            return left != null ? left : node.value;
        }
        return getSuccessorRecursive(node.right, key);
    }

    @Override
    public Optional<T> getPredecessor(T key) {
        Optional<T> floor = getFloor(key);
        if (!floor.isPresent()) return Optional.empty();

        T flr = floor.get();
        if (flr.compareTo(key) == 0) {
            return Optional.ofNullable(getPredecessorRecursive(root, key));
        }
        return Optional.of(flr);
    }

    private T getPredecessorRecursive(Node<T> node, T key) {
        if (node == null) return null;

        if (key.compareTo(node.value) > 0) {
            T right = getPredecessorRecursive(node.right, key);
            return right != null ? right : node.value;
        }
        return getPredecessorRecursive(node.left, key);
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

    @Override
    public int countInRange(T min, T max) {
        return getRange(min, max).size();
    }

    @Override
    public boolean containsAll(T... values) {
        if (values == null) return false;
        for (T value : values) {
            if (!search(value)) return false;
        }
        return true;
    }

    @Override
    public Iterator<T> iterator() {
        return new InOrderTraversal<>(root).traverse().iterator();
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

    public <R> R accept(NodeVisitor<T, R> visitor) {
        return visitor.visit(root);
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

    public static class BSTBuilder<T extends Comparable<T>> {
        private BinarySearchTree<T> tree = new BinarySearchTree<>();

        public BSTBuilder<T> add(T value) {
            tree.insert(value);
            return this;
        }

        @SafeVarargs
        public final BSTBuilder<T> addAll(T... values) {
            for (T value : values) {
                tree.insert(value);
            }
            return this;
        }

        public BinarySearchTree<T> build() {
            return tree;
        }
    }
}

class BinarySearchTreeDemo {
    public static void main(String[] args) {
        BinarySearchTree<Integer> bst = new BinarySearchTree<>();
        for (int val : new int[]{6, 2, 8, 7, 9, 1, 4, 3, 5}) {
            bst.insert(val);
        }

        System.out.println("=== Tree Overview ===");
        System.out.println("Tree: " + bst);
        System.out.println(bst.getAnalyzer().getMetrics());

        System.out.println("\n=== Traversals ===");
        System.out.println("In-order: " + bst.traverse(new InOrderTraversal<>(bst.getRoot())));
        System.out.println("Pre-order: " + bst.traverse(new PreOrderTraversal<>(bst.getRoot())));
        System.out.println("Post-order: " + bst.traverse(new PostOrderTraversal<>(bst.getRoot())));
        System.out.println("Level-order: " + bst.traverse(new LevelOrderTraversal<>(bst.getRoot())));

        System.out.println("\n=== Iterator (In-Order) ===");
        System.out.print("Values: ");
        for (Integer val : bst) {
            System.out.print(val + " ");
        }
        System.out.println();

        System.out.println("\n=== Advanced Queries ===");
        System.out.println("Floor(5): " + bst.getFloor(5));
        System.out.println("Ceiling(5): " + bst.getCeiling(5));
        System.out.println("Successor(5): " + bst.getSuccessor(5));
        System.out.println("Predecessor(5): " + bst.getPredecessor(5));
        System.out.println("Rank(5): " + bst.getRank(5));

        System.out.println("\n=== Range Operations ===");
        System.out.println("Range [3,7]: " + bst.getRange(3, 7));
        System.out.println("Count in [3,7]: " + bst.countInRange(3, 7));
        System.out.println("Contains all [2,5,9]: " + bst.containsAll(2, 5, 9));

        System.out.println("\n=== Validation ===");
        System.out.println(bst.getValidator().getValidationReport(bst.getRoot()));

        System.out.println("\n=== Modifications ===");
        bst.delete(1);
        bst.delete(6);
        System.out.println("After deleting 1,6: " + bst);
        System.out.println("New size: " + bst.size());

        System.out.println("\n=== String BST ===");
        BinarySearchTree<String> words = new BinarySearchTree<>();
        for (String word : new String[]{"dog", "cat", "zebra", "apple", "monkey", "banana"}) {
            words.insert(word);
        }
        System.out.println("Words: " + words);
        System.out.println("Successor of 'cat': " + words.getSuccessor("cat"));
        System.out.println("Predecessor of 'dog': " + words.getPredecessor("dog"));
        System.out.println("Range ['banana','dog']: " + words.getRange("banana", "dog"));
    }
}
