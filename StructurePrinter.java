class StructurePrinter<T extends Comparable<T>> implements TreeVisitor<T>
{
    @Override
    public void traverse(Node<T> root)
    {
        traverseHelper(root, 0);
    }

    private void traverseHelper(Node<T> root, int space)
    {
        int i;
        if(root != null)
        {
            space = space + 10;
            traverseHelper(root.right, space);

            System.out.printf("\n");

            for(i = 10; i < space; i++)
            {
                System.out.printf(" ");
            }

            System.out.printf("%s", root.data);
            System.out.printf("\n");
            traverseHelper(root.left, space);
        }
    }
}
