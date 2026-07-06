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

interface CodeProducingVisitor extends HuffmanNodeVisitor {
    Map<Character, String> codes();
}

@FunctionalInterface
interface VisitorFactory {
    CodeProducingVisitor create(List<Character> chars);
}

class CodeCollector implements CodeProducingVisitor {
    private final List<Character> chars;
    private final Map<Character, String> codes = new LinkedHashMap<>();

    CodeCollector(List<Character> chars) {
        this.chars = chars;
    }

    public void visit(LeafNode leaf, String prefix) {
        codes.put(chars.get(leaf.charIndex()), prefix.isEmpty() ? "0" : prefix);
    }

    public void visit(InternalNode node, String prefix) {
        node.left().accept(this, prefix + '0');
        node.right().accept(this, prefix + '1');
    }

    public Map<Character, String> codes() {
        return codes;
    }
}

@FunctionalInterface
interface TreeBuilder {
    HuffmanNode build(List<Character> chars, Map<Character, Integer> freqMap);
}

class HuffmanTreeBuilder implements TreeBuilder {
    public HuffmanNode build(List<Character> chars, Map<Character, Integer> freqMap) {
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

class HuffmanEncoder {

    private final FrequencyStrategy strategy;
    private final TreeBuilder treeBuilder;
    private final VisitorFactory visitorFactory;

    HuffmanEncoder(FrequencyStrategy strategy, TreeBuilder treeBuilder, VisitorFactory visitorFactory) {
        this.strategy = strategy;
        this.treeBuilder = treeBuilder;
        this.visitorFactory = visitorFactory;
    }

    HuffmanEncoder(FrequencyStrategy strategy) {
        this(strategy, new HuffmanTreeBuilder(), CodeCollector::new);
    }

    HuffmanResult encode() {
        Map<Character, Integer> freqMap = strategy.getFrequencies();
        List<Character> chars = new ArrayList<>(freqMap.keySet());

        HuffmanNode root = chars.size() == 1
            ? new LeafNode(freqMap.get(chars.get(0)), 0)
            : treeBuilder.build(chars, freqMap);

        CodeProducingVisitor visitor = visitorFactory.create(chars);
        root.accept(visitor, "");
        return new HuffmanResult(root, visitor.codes());
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
