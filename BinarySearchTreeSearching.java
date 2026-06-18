public final class BinarySearchTreeSearching {
    private static final int[] SAMPLE_VALUES = {6, 2, 8, 7, 9};
    private static final int SEARCH_KEY = 7;

    private BinarySearchTreeSearching() {
    }

    public static void main(String[] args) {
        BinarySearchTree tree = BinarySearchTree.fromValues(SAMPLE_VALUES);
        System.out.println(tree.contains(SEARCH_KEY));
    }
}
