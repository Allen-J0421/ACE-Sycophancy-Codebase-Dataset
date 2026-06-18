public final class BinarySearchTreeDemo {
    private static final Integer SEARCH_KEY = 7;
    private static final Integer[] SAMPLE_VALUES = {6, 2, 8, 7, 9};

    private BinarySearchTreeDemo() {
    }

    public static void main(String[] args) {
        BinarySearchTree<Integer> tree = BinarySearchTree.fromValues(SAMPLE_VALUES);

        System.out.println(tree.contains(SEARCH_KEY));
    }
}
