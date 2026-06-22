import java.util.ArrayList;

public final class HuffmanCodingDemo {
    private static final String DEMO_SYMBOLS = "abcdef";
    private static final int[] DEMO_FREQUENCIES = {5, 9, 12, 13, 16, 45};

    private HuffmanCodingDemo() {
        // Demo entry point.
    }

    public static void main(String[] args) {
        ArrayList<String> codes = HuffmanCoding.huffmanCodes(DEMO_SYMBOLS, DEMO_FREQUENCIES);
        System.out.print(String.join(" ", codes) + " ");
    }
}
