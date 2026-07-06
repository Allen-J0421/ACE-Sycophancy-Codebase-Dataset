class InsertionController<T extends Comparable<T>>
{
    private final RedBlackTree<T> tree;

    InsertionController(RedBlackTree<T> tree)
    {
        this.tree = tree;
    }

    public void insert(T data)
    {
        if(tree.getRoot() == null)
        {
            Node<T> node = new Node<>(data);
            node.setBlack();
            tree.setRoot(node);
        }
        else
        {
            RotationHandler<T> handler = new RotationHandler<>();
            tree.setRoot(insertHelp(tree.getRoot(), data, handler));
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
            if(root != tree.getRoot())
            {
                if(root.isRed() && root.left.isRed())
                    f = true;
            }
        }
        else
        {
            root.right = insertHelp(root.right, data, handler);
            root.right.parent = root;
            if(root != tree.getRoot())
            {
                if(root.isRed() && root.right.isRed())
                    f = true;
            }
        }

        root = handler.applyPending(root);

        if(f)
            handler.handleViolation(root, tree.getRoot());

        return root;
    }
}
