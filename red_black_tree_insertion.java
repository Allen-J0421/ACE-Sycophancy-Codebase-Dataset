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

    static class InsertionController<T extends Comparable<T>>
    {
        private final RedBlackTree<T> tree;

        InsertionController(RedBlackTree<T> tree)
        {
            this.tree = tree;
        }

        public void insert(T data)
        {
            if(tree.root == null)
            {
                tree.root = new Node<>(data);
                tree.root.setBlack();
            }
            else
            {
                RotationHandler<T> handler = new RotationHandler<>();
                tree.root = insertHelp(tree.root, data, handler);
            }
        }

        private Node<T> insertHelp(Node<T> root, T data, RotationHandler<T> handler)
        {
            boolean f = false;

            if(root == null)
                return new Node<>(data);
            else if(data.compareTo(root.data) < 0)
            {
                root.left = insertHelp(root.left, data, handler);
                root.left.parent = root;
                if(root != tree.root)
                {
                    if(root.isRed() && root.left.isRed())
                        f = true;
                }
            }
            else
            {
                root.right = insertHelp(root.right, data, handler);
                root.right.parent = root;
                if(root != tree.root)
                {
                    if(root.isRed() && root.right.isRed())
                        f = true;
                }
            }

            root = handler.applyPending(root);

            if(f)
                handler.handleViolation(root, tree.root);

            return root;
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
