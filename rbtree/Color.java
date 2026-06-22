package rbtree;

/**
 * The color of a {@link Node} in a red-black tree.
 *
 * <p>Replaces the original {@code char} sentinels ({@code 'R'} / {@code 'B'}),
 * which allowed any character to be assigned and gave no compile-time safety.
 */
public enum Color {
    RED,
    BLACK
}
