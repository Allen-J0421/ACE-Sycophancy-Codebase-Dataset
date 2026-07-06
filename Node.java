class Node<T> {
    private T data;
    private Node<T> left;
    private Node<T> right;

    Node(T data) {
        this.data = data;
    }

    T getData() { return data; }
    Node<T> getLeft() { return left; }
    Node<T> getRight() { return right; }
    void setLeft(Node<T> left) { this.left = left; }
    void setRight(Node<T> right) { this.right = right; }
}
