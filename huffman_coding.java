import java.util.*;

class FrequencyAnalyzer {
    private FrequencyAnalyzer() {}

    static Map<Character, Integer> countChars(String input) {
        Map<Character, Integer> freq = new LinkedHashMap<>();
        for (char c : input.toCharArray()) {
            freq.merge(c, 1, Integer::sum);
        }
        return freq;
    }
}

@FunctionalInterface
interface FrequencyStrategy {
    Map<Character, Integer> getFrequencies();

    static FrequencyStrategy fromString(String input) {
        return () -> FrequencyAnalyzer.countChars(input);
    }

    static FrequencyStrategy fromPreset(Map<Character, Integer> preset) {
        return () -> new LinkedHashMap<>(preset);
    }
}

record HuffmanResult(HuffmanNode tree, Map<Character, String> codes) {}

class HuffmanEncoder {

    private final FrequencyStrategy strategy;

    HuffmanEncoder(FrequencyStrategy strategy) {
        this.strategy = strategy;
    }

    HuffmanResult encode() {
        Map<Character, Integer> freqMap = strategy.getFrequencies();
        List<Character> chars = new ArrayList<>(freqMap.keySet());

        if (chars.size() == 1) {
            LeafNode root = new LeafNode(freqMap.get(chars.get(0)), 0);
            return new HuffmanResult(root, Collections.singletonMap(chars.get(0), "0"));
        }

        HuffmanNode root = buildTree(chars, freqMap);
        Map<Character, String> codes = new LinkedHashMap<>();
        root.collectCodes("", codes, chars);
        return new HuffmanResult(root, codes);
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
        HuffmanResult presetResult = new HuffmanEncoder(FrequencyStrategy.fromPreset(preset)).encode();
        presetResult.codes().forEach((ch, code) -> System.out.println(ch + ": " + code));

        System.out.println("\nFrom raw string (\"mississippi\"):");
        HuffmanResult stringResult = new HuffmanEncoder(FrequencyStrategy.fromString("mississippi")).encode();
        stringResult.codes().forEach((ch, code) -> System.out.println(ch + ": " + code));
    }
}
