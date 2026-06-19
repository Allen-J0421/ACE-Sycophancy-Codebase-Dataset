/**
 * Tests for input normalization strategies.
 */
class TestInputNormalizers {

    public static void main(String[] args) {
        testIndividualNormalizers();
        testCompositeNormalizers();
        testNormalizingSolver();
        testBuilder();
        System.out.println("\n✓ All normalizer tests passed");
    }

    static void testIndividualNormalizers() {
        System.out.println("=== Individual Normalizers ===\n");

        // Test lowercase
        InputNormalizer lower = new LowercaseNormalizer();
        assert lower.normalize("HeLLo WoRLd").equals("hello world");
        System.out.println("✓ LowercaseNormalizer");

        // Test trim
        InputNormalizer trim = new TrimNormalizer();
        assert trim.normalize("  hello  ").equals("hello");
        System.out.println("✓ TrimNormalizer");

        // Test collapse whitespace
        InputNormalizer collapse = new CollapseWhitespaceNormalizer();
        assert collapse.normalize("  hello   world  ").equals("hello world");
        System.out.println("✓ CollapseWhitespaceNormalizer");

        // Test remove whitespace
        InputNormalizer removeWs = new RemoveWhitespaceNormalizer();
        assert removeWs.normalize("hello world").equals("helloworld");
        System.out.println("✓ RemoveWhitespaceNormalizer");

        // Test remove punctuation
        InputNormalizer removePunct = new RemovePunctuationNormalizer();
        assert removePunct.normalize("Hello, World!").equals("Hello World");
        System.out.println("✓ RemovePunctuationNormalizer");

        // Test remove digits
        InputNormalizer removeDigits = new RemoveDigitsNormalizer();
        assert removeDigits.normalize("Hello123World456").equals("HelloWorld");
        System.out.println("✓ RemoveDigitsNormalizer");

        System.out.println();
    }

    static void testCompositeNormalizers() {
        System.out.println("=== Composite Normalizers ===\n");

        // Test chaining with andThen
        InputNormalizer chain = new LowercaseNormalizer()
                .andThen(new TrimNormalizer())
                .andThen(new CollapseWhitespaceNormalizer());
        String result = chain.normalize("  HeLLo   WoRLD  ");
        assert result.equals("hello world");
        System.out.println("✓ Chained normalizers (lowercase → trim → collapse)");

        // Test preset: textOnly
        InputNormalizer textOnly = InputNormalizers.textOnly();
        String result2 = textOnly.normalize("Hello, World 123!");
        assert result2.equals("hello world");
        System.out.println("✓ Preset: textOnly");

        // Test preset: strictComparison
        InputNormalizer strict = InputNormalizers.strictComparison();
        String result3 = strict.normalize("  HeLLo   WoRLD  ");
        assert result3.equals("hello world");
        System.out.println("✓ Preset: strictComparison");

        // Test composite constructor
        InputNormalizer composite = InputNormalizers.compose(
                new LowercaseNormalizer(),
                new RemoveWhitespaceNormalizer()
        );
        String result4 = composite.normalize("HeLLo WoRLD");
        assert result4.equals("helloworld");
        System.out.println("✓ Composite via constructor");

        System.out.println();
    }

    static void testNormalizingSolver() {
        System.out.println("=== Normalizing Solvers ===\n");

        // Test case-insensitive solver
        LcsSolver caseInsensitive = NormalizedSolvers.caseInsensitive();
        LcsInput input1 = new LcsInput("AGGTAB", "aggtab");
        LcsResult result1 = caseInsensitive.solve(input1);
        assert result1.getLength() == 6 : "Should match completely (case-insensitive)";
        System.out.println("✓ Case-insensitive solver");

        // Test whitespace-insensitive solver
        LcsSolver whitespaceInsensitive = NormalizedSolvers.whitespaceInsensitive();
        LcsInput input2 = new LcsInput("A G G T A B", "AGGTAB");
        LcsResult result2 = whitespaceInsensitive.solve(input2);
        assert result2.getLength() == 6 : "Should match (whitespace-insensitive)";
        System.out.println("✓ Whitespace-insensitive solver");

        // Test text-only solver
        LcsSolver textOnly = NormalizedSolvers.textOnly();
        LcsInput input3 = new LcsInput("Hello, World!", "hello world");
        LcsResult result3 = textOnly.solve(input3);
        assert result3.getLength() == 10 : "Should match (text-only, ignoring punctuation)";
        System.out.println("✓ Text-only solver");

        System.out.println();
    }

    static void testBuilder() {
        System.out.println("=== Builder Patterns ===\n");

        // Test input normalizer builder
        InputNormalizer custom = InputNormalizers.builder()
                .lowercase()
                .trim()
                .removeWhitespace()
                .build();
        String result1 = custom.normalize("  HeLLo WoRLD  ");
        assert result1.equals("helloworld");
        System.out.println("✓ InputNormalizer builder");

        // Test normalized solver builder
        LcsSolver customSolver = NormalizedSolvers.builder()
                .cachedSolver()
                .caseInsensitive()
                .build();
        LcsInput input = new LcsInput("HELLO", "hello");
        LcsResult result = customSolver.solve(input);
        assert result.getLength() == 5;
        System.out.println("✓ NormalizedSolver builder");

        // Test builder with custom normalizer
        LcsSolver customSolver2 = NormalizedSolvers.builder()
                .standardSolver()
                .customNormalizer(InputNormalizers.textOnly())
                .build();
        LcsInput input2 = new LcsInput("Test, 123!", "test");
        LcsResult result2 = customSolver2.solve(input2);
        assert result2.getLength() == 4; // "test" matches after normalization
        System.out.println("✓ NormalizedSolver builder with custom normalizer");

        System.out.println();
    }
}
