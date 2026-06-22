import java.util.ArrayList;

public final class HuffmanCodingDemo {
    private HuffmanCodingDemo() {
        // Demo entry point.
    }

    public static void main(String[] args) {
        String symbols = "abcdef";
        int[] frequencies = {5, 9, 12, 13, 16, 45};
        ArrayList<String> codes = HuffmanCoding.huffmanCodes(symbols, frequencies);
        for (String code : codes) {
            System.out.print(code + " ");
        }
    }
}
