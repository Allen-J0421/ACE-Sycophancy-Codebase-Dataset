class Node {
    int data;
    Node left, right;

    public Node(int item) {
        data = item;
        left = right = null;
    }
}
class BinarySearchTree {
    static boolean search(Node root, int key) {
        boolean present = false;

        while( root != null ) {
            if( root.data == key ) {
                present = true;
                break;
            }

            else if( key > root.data )
                root = root.right;

            else root = root.left;
        }
        return present;
    }

    public static void main(String[] args) {

        Node root = new Node(6);
        root.left = new Node(2);
        root.right = new Node(8);
        root.right.left = new Node(7);
        root.right.right = new Node(9);

        int key = 7;

        System.out.println(search(root, key));
    }
}
