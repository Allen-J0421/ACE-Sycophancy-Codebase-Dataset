import java.io.*;

public class RedBlackTree<T extends Comparable<T>> implements IBinarySearchTree<T> {
    private Node<T> root;

    private final InsertionController<T> controller;

    public RedBlackTree()
    {
        root = null;
        controller = new InsertionController<>(this);
    }

    @Override
    public Node<T> getRoot()
    {
        return root;
    }

    void setRoot(Node<T> node)
    {
        root = node;
    }

    @Override
    public void insert(T data)
    {
        controller.insert(data);
    }

    public static void main(String[] args)
    {
        IBinarySearchTree<Integer> t = new RedBlackTree<>();
        TreeVisitor<Integer> inorder = new InorderPrinter<>();
        TreeVisitor<Integer> structure = new StructurePrinter<>();
        Integer[] arr = {1,4,6,3,5,7,8,2,9};
        for(int i = 0; i < 9; i++)
        {
            t.insert(arr[i]);
            System.out.println();
            inorder.traverse(t.getRoot());
        }

        structure.traverse(t.getRoot());
    }
}
