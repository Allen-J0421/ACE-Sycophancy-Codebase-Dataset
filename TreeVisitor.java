interface TreeVisitor<T extends Comparable<T>>
{
    void traverse(Node<T> root);
}
