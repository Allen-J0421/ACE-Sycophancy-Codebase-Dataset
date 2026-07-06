import java.io.*;

public class RedBlackTree {
    public Node root;

    enum Color { RED, BLACK }

    static class Node
    {
        int data;
        Node left;
        Node right;
        Color colour;
        Node parent;

        Node(int data)
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

    static class RotationHandler
    {
        enum RotationCase { NONE, LL, RR, LR, RL }

        private RotationCase pending = RotationCase.NONE;

        void set(RotationCase rc) { pending = rc; }

        private Node rotateLeft(Node node)
        {
            Node x = node.right;
            Node y = x.left;
            x.left = node;
            node.right = y;
            node.parent = x;
            if(y != null)
                y.parent = node;
            return x;
        }

        private Node rotateRight(Node node)
        {
            Node x = node.left;
            Node y = x.right;
            x.right = node;
            node.left = y;
            node.parent = x;
            if(y != null)
                y.parent = node;
            return x;
        }

        Node applyPending(Node root)
        {
            if(pending == RotationCase.LL)
            {
                root = rotateLeft(root);
                root.setBlack();
                root.left.setRed();
            }
            else if(pending == RotationCase.RR)
            {
                root = rotateRight(root);
                root.setBlack();
                root.right.setRed();
            }
            else if(pending == RotationCase.RL)
            {
                root.right = rotateRight(root.right);
                root.right.parent = root;
                root = rotateLeft(root);
                root.setBlack();
                root.left.setRed();
            }
            else if(pending == RotationCase.LR)
            {
                root.left = rotateLeft(root.left);
                root.left.parent = root;
                root = rotateRight(root);
                root.setBlack();
                root.right.setRed();
            }
            pending = RotationCase.NONE;
            return root;
        }

        void handleViolation(Node node, Node treeRoot)
        {
            if(node.parent.right == node)
            {
                if(node.parent.left == null || node.parent.left.isBlack())
                {
                    if(node.left != null && node.left.isRed())
                        pending = RotationCase.RL;
                    else if(node.right != null && node.right.isRed())
                        pending = RotationCase.LL;
                }
                else
                {
                    node.parent.left.setBlack();
                    node.setBlack();
                    if(node.parent != treeRoot)
                        node.parent.setRed();
                }
            }
            else
            {
                if(node.parent.right == null || node.parent.right.isBlack())
                {
                    if(node.left != null && node.left.isRed())
                        pending = RotationCase.RR;
                    else if(node.right != null && node.right.isRed())
                        pending = RotationCase.LR;
                }
                else
                {
                    node.parent.right.setBlack();
                    node.setBlack();
                    if(node.parent != treeRoot)
                        node.parent.setRed();
                }
            }
        }
    }

    public RedBlackTree()
    {
        root = null;
    }

    Node insertHelp(Node root, int data, RotationHandler handler)
    {
        boolean f = false;

        if(root == null)
            return new Node(data);
        else if(data < root.data)
        {
            root.left = insertHelp(root.left, data, handler);
            root.left.parent = root;
            if(root != this.root)
            {
                if(root.isRed() && root.left.isRed())
                    f = true;
            }
        }
        else
        {
            root.right = insertHelp(root.right, data, handler);
            root.right.parent = root;
            if(root != this.root)
            {
                if(root.isRed() && root.right.isRed())
                    f = true;
            }
        }

        root = handler.applyPending(root);

        if(f)
            handler.handleViolation(root, this.root);

        return root;
    }

    public void insert(int data)
    {
        if(this.root == null)
        {
            this.root = new Node(data);
            this.root.setBlack();
        }
        else
        {
            RotationHandler handler = new RotationHandler();
            this.root = insertHelp(this.root, data, handler);
        }
    }

    static class TreePrinter
    {
        public void inorderTraversal(Node root)
        {
            if(root != null)
            {
                inorderTraversal(root.left);
                System.out.printf("%d ", root.data);
                inorderTraversal(root.right);
            }
        }

        public void printTree(Node root)
        {
            printTreeHelper(root, 0);
        }

        private void printTreeHelper(Node root, int space)
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

                System.out.printf("%d", root.data);
                System.out.printf("\n");
                printTreeHelper(root.left, space);
            }
        }
    }

    public static void main(String[] args)
    {
        RedBlackTree t = new RedBlackTree();
        TreePrinter printer = new TreePrinter();
        int[] arr = {1,4,6,3,5,7,8,2,9};
        for(int i = 0; i < 9; i++)
        {
            t.insert(arr[i]);
            System.out.println();
            printer.inorderTraversal(t.root);
        }

        printer.printTree(t.root);
    }
}
