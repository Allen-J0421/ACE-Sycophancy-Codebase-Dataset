import java.io.*;

public class RedBlackTree<T extends Comparable<T>> implements IBinarySearchTree<T> {
    private Node<T> root;

    static class TreePrinter<T extends Comparable<T>>
    {
        public void inorderTraversal(Node<T> root)
        {
            if(root != null)
            {
                inorderTraversal(root.left);
                System.out.printf("%s ", root.data);
                inorderTraversal(root.right);
            }
        }

        public void printTree(Node<T> root)
        {
            printTreeHelper(root, 0);
        }

        private void printTreeHelper(Node<T> root, int space)
        {
            int i;
            if(root != null)
            {
                space = space + 10;
                printTreeHelper(root.right, space);

                System.out.printf("\n");

                for(i = 10; i < space; i++)
                {
                    System.out.printf(" ");
                }

                System.out.printf("%s", root.data);
                System.out.printf("\n");
                printTreeHelper(root.left, space);
            }
        }
    }

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
        TreePrinter<Integer> printer = new TreePrinter<>();
        Integer[] arr = {1,4,6,3,5,7,8,2,9};
        for(int i = 0; i < 9; i++)
        {
            t.insert(arr[i]);
            System.out.println();
            printer.inorderTraversal(t.getRoot());
        }

        printer.printTree(t.getRoot());
    }
}
