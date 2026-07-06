interface IBinarySearchTree<T extends Comparable<T>>
{
    void insert(T data);
    Node<T> getRoot();
}
