import java.util.*;

class Node {

    int data;

    int index;

    Node left, right;

    Node(int d, int i) {
        data = d;
        index = i;
        left = right = null;
    }

    Node(Node l, Node r) {
        data = l.data + r.data;

        index = Math.min(l.index, r.index);

        left = l;
        right = r;
    }
}

class Compare implements Comparator<Node> {
    public int compare(Node a, Node b) {

        if (a.data != b.data)
            return a.data - b.data;

        return a.index - b.index;
    }
}

public class HuffmanCoding {

    static void preOrder(Node root, ArrayList<String> ans, String curr) {
        if (root == null) return;

        if (root.left == null && root.right == null) {

            if (curr.equals("")) curr = "0";

            ans.add(curr);
            return;
        }

        preOrder(root.left, ans, curr + '0');
        preOrder(root.right, ans, curr + '1');
    }

    static ArrayList<String> huffmanCodes(String s, int[] freq) {

        int n = s.length();

        PriorityQueue<Node> pq = new PriorityQueue<>(new Compare());
        for (int i = 0; i < n; i++) {

            Node tmp = new Node(freq[i], i);

            pq.add(tmp);
        }

        if (n == 1) {
            ArrayList<String> res = new ArrayList<>();
            res.add("0");
            return res;
        }

        while (pq.size() >= 2) {

            Node l = pq.poll();

            Node r = pq.poll();

            Node newNode = new Node(l, r);

            pq.add(newNode);
        }

        Node root = pq.peek();
        ArrayList<String> ans = new ArrayList<>();
        preOrder(root, ans, "");
        return ans;
    }

    public static void main(String[] args) {
        String s = "abcdef";
        int[] freq = {5, 9, 12, 13, 16, 45};
        ArrayList<String> ans = huffmanCodes(s, freq);
        for (int i = 0; i < ans.size(); i++) {
            System.out.print(ans.get(i) + " ");
        }
    }
}
