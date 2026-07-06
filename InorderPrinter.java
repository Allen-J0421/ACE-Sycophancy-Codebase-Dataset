class InorderPrinter<T extends Comparable<T>> implements TreeVisitor<T>
{
    @Override
    public void traverse(Node<T> root)
    {
        if(root != null)
        {
            traverse(root.left);
            System.out.printf("%s ", root.data);
            traverse(root.right);
        }
    }
}
