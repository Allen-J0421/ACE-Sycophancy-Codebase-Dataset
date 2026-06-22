import java.util.StringJoiner;

/**
 * Small demonstration of {@link AVLTree}: inserts a sequence of keys that
 * triggers each kind of rotation, then prints the resulting tree in pre-order.
 */
public class Main {

    public static void main(String[] args) {
        AVLTree<Integer> tree = new AVLTree<>();

        for (int key : new int[] {10, 20, 30, 40, 50, 25}) {
            tree.insert(key);
        }

        System.out.println(join(tree.preOrder()));
    }

    private static String join(Iterable<?> values) {
        StringJoiner joiner = new StringJoiner(" ");
        for (Object value : values) {
            joiner.add(String.valueOf(value));
        }
        return joiner.toString();
    }
}
