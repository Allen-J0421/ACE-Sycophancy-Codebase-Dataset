enum Color { RED, BLACK }

class Node<T extends Comparable<T>>
{
    T data;
    Node<T> left;
    Node<T> right;
    Color colour;
    Node<T> parent;

    Node(T data)
    {
        this.data = data;
        this.left = null;
        this.right = null;
        this.colour = Color.RED;
        this.parent = null;
    }

    boolean isRed() { return colour == Color.RED; }
    boolean isBlack() { return colour == Color.BLACK; }
    void setRed() { colour = Color.RED; }
    void setBlack() { colour = Color.BLACK; }
}
