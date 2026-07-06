import java.io.*;

public class RedBlackTree {
    public Node root;

    enum Color { RED, BLACK }

    enum RotationCase { NONE, LL, RR, LR, RL }

    public RedBlackTree()
    {
        root = null;
    }

    class Node
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

    Node rotateLeft(Node node)
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

    Node rotateRight(Node node)
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

    Node insertHelp(Node root, int data, RotationCase[] pending)
    {
        boolean f = false;

        if(root == null)
            return new Node(data);
        else if(data < root.data)
        {
            root.left = insertHelp(root.left, data, pending);
            root.left.parent = root;
            if(root != this.root)
            {
                if(root.isRed() && root.left.isRed())
                    f = true;
            }
        }
        else
        {
            root.right = insertHelp(root.right, data, pending);
            root.right.parent = root;
            if(root != this.root)
            {
                if(root.isRed() && root.right.isRed())
                    f = true;
            }
        }

        if(pending[0] == RotationCase.LL)
        {
            root = rotateLeft(root);
            root.setBlack();
            root.left.setRed();
            pending[0] = RotationCase.NONE;
        }
        else if(pending[0] == RotationCase.RR)
        {
            root = rotateRight(root);
            root.setBlack();
            root.right.setRed();
            pending[0] = RotationCase.NONE;
        }
        else if(pending[0] == RotationCase.RL)
        {
            root.right = rotateRight(root.right);
            root.right.parent = root;
            root = rotateLeft(root);
            root.setBlack();
            root.left.setRed();
            pending[0] = RotationCase.NONE;
        }
        else if(pending[0] == RotationCase.LR)
        {
            root.left = rotateLeft(root.left);
            root.left.parent = root;
            root = rotateRight(root);
            root.setBlack();
            root.right.setRed();
            pending[0] = RotationCase.NONE;
        }

        if(f)
        {
            if(root.parent.right == root)
            {
                if(root.parent.left == null || root.parent.left.isBlack())
                {
                    if(root.left != null && root.left.isRed())
                        pending[0] = RotationCase.RL;
                    else if(root.right != null && root.right.isRed())
                        pending[0] = RotationCase.LL;
                }
                else
                {
                    root.parent.left.setBlack();
                    root.setBlack();
                    if(root.parent != this.root)
                        root.parent.setRed();
                }
            }
            else
            {
                if(root.parent.right == null || root.parent.right.isBlack())
                {
                    if(root.left != null && root.left.isRed())
                        pending[0] = RotationCase.RR;
                    else if(root.right != null && root.right.isRed())
                        pending[0] = RotationCase.LR;
                }
                else
                {
                    root.parent.right.setBlack();
                    root.setBlack();
                    if(root.parent != this.root)
                        root.parent.setRed();
                }
            }
            f = false;
        }
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
            RotationCase[] pending = { RotationCase.NONE };
            this.root = insertHelp(this.root, data, pending);
        }
    }

    void inorderTraversalHelper(Node node)
    {
        if(node != null)
        {
            inorderTraversalHelper(node.left);
            System.out.printf("%d ", node.data);
            inorderTraversalHelper(node.right);
        }
    }

    public void inorderTraversal()
    {
        inorderTraversalHelper(this.root);
    }

    void printTreeHelper(Node root, int space)
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

    public void printTree()
    {
        printTreeHelper(this.root, 0);
    }

    public static void main(String[] args)
    {
        RedBlackTree t = new RedBlackTree();
        int[] arr = {1,4,6,3,5,7,8,2,9};
        for(int i = 0; i < 9; i++)
        {
            t.insert(arr[i]);
            System.out.println();
            t.inorderTraversal();
        }

        t.printTree();
    }
}
