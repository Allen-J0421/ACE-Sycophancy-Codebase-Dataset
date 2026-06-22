import java.util.ArrayList;

public final class huffman_coding {
    private huffman_coding() {
        // Compatibility entry point.
    }

    public static ArrayList<String> huffmanCodes(String s, int[] freq) {
        return HuffmanCoding.huffmanCodes(s, freq);
    }

    public static void main(String[] args) {
        HuffmanCodingDemo.main(args);
    }
}
