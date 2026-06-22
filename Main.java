import java.util.List;
import java.util.StringJoiner;

final class Main {
    public static void main(String[] args) {
        BTree<Integer> tree = createDemoTree();
        printTraversal(tree);
        printSearchResult(tree, 6);
        printSearchResult(tree, 15);
    }

    private static BTree<Integer> createDemoTree() {
        BTree<Integer> tree = new BTree<>(3);
        Integer[] values = {10, 20, 5, 6, 12, 30, 7, 17};
        for (Integer value : values) {
            tree.insert(value);
        }
        return tree;
    }

    private static void printTraversal(BTree<Integer> tree) {
        System.out.print("Traversal of the constructed tree is ");
        System.out.print(formatKeys(tree.keysInOrder()));
        System.out.println();
    }

    private static void printSearchResult(BTree<Integer> tree, int key) {
        if (tree.contains(key)) {
            System.out.println(" | Present");
        } else {
            System.out.println(" | Not Present");
        }
    }

    private static String formatKeys(List<Integer> keys) {
        StringJoiner joiner = new StringJoiner(" ");
        for (Integer key : keys) {
            joiner.add(Integer.toString(key));
        }
        return joiner.toString();
    }
}
