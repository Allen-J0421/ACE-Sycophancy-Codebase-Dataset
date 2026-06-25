final class AVLTreeDemo {
    private AVLTreeDemo() {
    }

    public static void main(String[] args) {
        AVLTree<Integer> tree = AVLTree.createWithNaturalOrder();
        int[] values = {10, 20, 30, 40, 50, 25};

        for (int value : values) {
            tree.insert(value);
        }

        System.out.println(tree.preOrderString());
    }
}
