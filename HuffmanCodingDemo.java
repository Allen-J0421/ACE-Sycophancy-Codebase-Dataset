import java.util.ArrayList;

public final class HuffmanCodingDemo {
    private HuffmanCodingDemo() {
        // Demo entry point.
    }

    public static void main(String[] args) {
        ArrayList<String> codes =
                HuffmanCoding.huffmanCodes(HuffmanExamples.DEMO_SYMBOLS, HuffmanExamples.DEMO_FREQUENCIES);
        System.out.print(String.join(" ", codes) + " ");
    }
}
