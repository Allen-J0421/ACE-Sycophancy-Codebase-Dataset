import java.util.*;

@FunctionalInterface
interface FrequencyStrategy {
    Map<Character, Integer> getFrequencies();

    static FrequencyStrategy fromString(String input) {
        return () -> {
            Map<Character, Integer> freq = new LinkedHashMap<>();
            for (char c : input.toCharArray()) {
                freq.merge(c, 1, Integer::sum);
            }
            return freq;
        };
    }

    static FrequencyStrategy fromPreset(Map<Character, Integer> preset) {
        return () -> new LinkedHashMap<>(preset);
    }
}

class HuffmanEncoder {

    private sealed interface HuffmanNode permits LeafNode, InternalNode {
        int frequency();
        int charIndex();
        void collectCodes(String prefix, Map<Character, String> codes, List<Character> chars);
    }

    private static final class LeafNode implements HuffmanNode {
        private final int frequency;
        private final int charIndex;

        LeafNode(int frequency, int charIndex) {
            this.frequency = frequency;
            this.charIndex = charIndex;
        }

        public int frequency() { return frequency; }
        public int charIndex() { return charIndex; }

        public void collectCodes(String prefix, Map<Character, String> codes, List<Character> chars) {
            codes.put(chars.get(charIndex), prefix.isEmpty() ? "0" : prefix);
        }
    }

    private static final class InternalNode implements HuffmanNode {
        private final int frequency;
        private final int charIndex;
        private final HuffmanNode left;
        private final HuffmanNode right;

        InternalNode(HuffmanNode left, HuffmanNode right) {
            this.frequency = left.frequency() + right.frequency();
            this.charIndex = Math.min(left.charIndex(), right.charIndex());
            this.left = left;
            this.right = right;
        }

        public int frequency() { return frequency; }
        public int charIndex() { return charIndex; }

        public void collectCodes(String prefix, Map<Character, String> codes, List<Character> chars) {
            left.collectCodes(prefix + '0', codes, chars);
            right.collectCodes(prefix + '1', codes, chars);
        }
    }

    private final FrequencyStrategy strategy;

    HuffmanEncoder(FrequencyStrategy strategy) {
        this.strategy = strategy;
    }

    Map<Character, String> encode() {
        Map<Character, Integer> freqMap = strategy.getFrequencies();
        List<Character> chars = new ArrayList<>(freqMap.keySet());

        if (chars.size() == 1) {
            return Collections.singletonMap(chars.get(0), "0");
        }

        HuffmanNode root = buildTree(chars, freqMap);
        Map<Character, String> codes = new LinkedHashMap<>();
        root.collectCodes("", codes, chars);
        return codes;
    }

    private HuffmanNode buildTree(List<Character> chars, Map<Character, Integer> freqMap) {
        PriorityQueue<HuffmanNode> pq = new PriorityQueue<>(
            Comparator.comparingInt((HuffmanNode n) -> n.frequency())
                      .thenComparingInt(n -> n.charIndex())
        );
        for (int i = 0; i < chars.size(); i++) {
            pq.add(new LeafNode(freqMap.get(chars.get(i)), i));
        }
        while (pq.size() >= 2) {
            HuffmanNode left = pq.poll();
            HuffmanNode right = pq.poll();
            pq.add(new InternalNode(left, right));
        }
        return pq.peek();
    }
}

public class HuffmanCoding {

    public static void main(String[] args) {
        Map<Character, Integer> preset = new LinkedHashMap<>();
        preset.put('a', 5);  preset.put('b', 9);  preset.put('c', 12);
        preset.put('d', 13); preset.put('e', 16); preset.put('f', 45);

        System.out.println("From preset frequencies:");
        new HuffmanEncoder(FrequencyStrategy.fromPreset(preset))
            .encode()
            .forEach((ch, code) -> System.out.println(ch + ": " + code));

        System.out.println("\nFrom raw string (\"mississippi\"):");
        new HuffmanEncoder(FrequencyStrategy.fromString("mississippi"))
            .encode()
            .forEach((ch, code) -> System.out.println(ch + ": " + code));
    }
}
