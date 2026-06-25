public final class AvlTreeInsertionDemo {
    private AvlTreeInsertionDemo() {
    }

    public static void main(String[] args) {
        AVLTree<Integer> tree = AVLTree.fromKeys(10, 20, 30, 40, 50, 25);
        System.out.print(tree.preOrder());
    }
}
