public class UnionFindTest {

    public static void main(String[] args) {
        testBasicUnion();
        testConnectivity();
        testMultipleUnions();
        testValidation();
        testStatistics();
        testReset();
        testBuilder();
        testStrategies();
        testFindStrategies();
        testPresets();
        testPerformanceMetrics();
        testOperationListeners();
        testExceptionHandling();
        testSafeUnionFind();
        System.out.println("\n✓ All tests passed");
    }

    private static void testBasicUnion() {
        UnionFind uf = UnionFind.builder().withSize(5).build();
        uf.union(1, 2);
        uf.union(3, 4);
        assert uf.isConnected(1, 2) : "1 and 2 should be connected";
        assert !uf.isConnected(2, 4) : "2 and 4 should not be connected";
        System.out.println("✓ testBasicUnion passed");
    }

    private static void testConnectivity() {
        UnionFind uf = UnionFind.builder().withSize(6).build();
        uf.union(0, 1);
        uf.union(1, 2);
        uf.union(3, 4);
        assert uf.isConnected(0, 2) : "0 and 2 should be transitively connected";
        assert !uf.isConnected(2, 3) : "2 and 3 should not be connected";
        System.out.println("✓ testConnectivity passed");
    }

    private static void testMultipleUnions() {
        UnionFind uf = UnionFind.builder().withSize(10).build();
        uf.union(1, 2);
        uf.union(2, 3);
        uf.union(4, 5);
        uf.union(5, 6);
        uf.union(3, 4);
        assert uf.isConnected(1, 6) : "1 and 6 should be connected after union chain";
        System.out.println("✓ testMultipleUnions passed");
    }

    private static void testValidation() {
        testInvalidIndex();
        testInvalidSize();
        System.out.println("✓ testValidation passed");
    }

    private static void testInvalidIndex() {
        UnionFind uf = UnionFind.builder().withSize(5).build();
        try {
            uf.find(-1);
            assert false : "Should throw InvalidIndexException";
        } catch (UnionFindException.InvalidIndexException e) {
            // Expected
        }

        try {
            uf.union(0, 5);
            assert false : "Should throw InvalidIndexException";
        } catch (UnionFindException.InvalidIndexException e) {
            // Expected
        }
    }

    private static void testInvalidSize() {
        try {
            UnionFind.builder().withSize(0).build();
            assert false : "Should throw InvalidSizeException";
        } catch (UnionFindException.InvalidSizeException e) {
            // Expected
        }

        try {
            UnionFind.builder().withSize(-1).build();
            assert false : "Should throw InvalidSizeException";
        } catch (UnionFindException.InvalidSizeException e) {
            // Expected
        }
    }

    private static void testStatistics() {
        UnionFind uf = UnionFind.builder().withSize(5).build();
        uf.union(1, 2);
        uf.union(2, 3);
        uf.isConnected(1, 3);
        DisjointSet.Statistics stats = uf.getStatistics();
        assert stats.unionOperations == 2 : "Should have 2 union operations";
        assert stats.findOperations >= 3 : "Should have at least 3 find operations";
        System.out.println("✓ testStatistics passed (" + stats + ")");
    }

    private static void testReset() {
        UnionFind uf = UnionFind.builder().withSize(5).build();
        uf.union(1, 2);
        uf.union(2, 3);
        DisjointSet.Statistics statsBefore = uf.getStatistics();
        assert statsBefore.unionOperations == 2 : "Should have recorded operations";

        uf.reset();
        DisjointSet.Statistics statsAfter = uf.getStatistics();
        assert statsAfter.findOperations == 0 : "Statistics should be reset";
        assert statsAfter.unionOperations == 0 : "Statistics should be reset";
        assert !uf.isConnected(1, 2) : "Connections should be reset";
        System.out.println("✓ testReset passed");
    }

    private static void testBuilder() {
        try {
            UnionFind.builder().build();
            assert false : "Should throw InvalidSizeException when size not set";
        } catch (UnionFindException.InvalidSizeException e) {
            // Expected
        }

        try {
            UnionFind.builder().withSize(0).build();
            assert false : "Should throw InvalidSizeException for non-positive size";
        } catch (UnionFindException.InvalidSizeException e) {
            // Expected
        }

        UnionFind uf = UnionFind.builder().withSize(10).build();
        assert uf.getSize() == 10 : "Builder should create UnionFind with correct size";
        System.out.println("✓ testBuilder passed");
    }

    private static void testStrategies() {
        testRankBasedStrategy();
        testSimpleStrategy();
    }

    private static void testRankBasedStrategy() {
        UnionFind uf = UnionFind.builder()
            .withSize(5)
            .withStrategy(new RankBasedUnionStrategy())
            .build();
        uf.union(1, 2);
        uf.union(2, 3);
        assert uf.isConnected(1, 3) : "Rank-based strategy should maintain connections";
        System.out.println("✓ testRankBasedStrategy passed");
    }

    private static void testSimpleStrategy() {
        UnionFind uf = UnionFind.builder()
            .withSize(5)
            .withStrategy(new SimpleUnionStrategy())
            .build();
        uf.union(1, 2);
        uf.union(2, 3);
        assert uf.isConnected(1, 3) : "Simple strategy should maintain connections";
        System.out.println("✓ testSimpleStrategy passed");
    }

    private static void testFindStrategies() {
        testPathCompressionFindStrategy();
        testSimpleFindStrategy();
    }

    private static void testPathCompressionFindStrategy() {
        UnionFind uf = UnionFind.builder()
            .withSize(5)
            .withUnionStrategy(new RankBasedUnionStrategy())
            .withFindStrategy(new PathCompressionFindStrategy())
            .build();
        uf.union(0, 1);
        uf.union(1, 2);
        uf.union(2, 3);
        assert uf.isConnected(0, 3) : "Path compression should maintain connectivity";
        System.out.println("✓ testPathCompressionFindStrategy passed");
    }

    private static void testSimpleFindStrategy() {
        UnionFind uf = UnionFind.builder()
            .withSize(5)
            .withUnionStrategy(new SimpleUnionStrategy())
            .withFindStrategy(new SimpleFindStrategy())
            .build();
        uf.union(0, 1);
        uf.union(1, 2);
        assert uf.isConnected(0, 2) : "Simple find should maintain connectivity";
        System.out.println("✓ testSimpleFindStrategy passed");
    }

    private static void testPresets() {
        testOptimalPreset();
        testMinimalPreset();
        testBalancedPreset();
    }

    private static void testOptimalPreset() {
        UnionFind uf = UnionFind.builder()
            .withSize(5)
            .withOptimal()
            .build();
        uf.union(1, 2);
        uf.union(2, 3);
        assert uf.isConnected(1, 3) : "Optimal preset should work";
        System.out.println("✓ testOptimalPreset passed");
    }

    private static void testMinimalPreset() {
        UnionFind uf = UnionFind.builder()
            .withSize(5)
            .withMinimal()
            .build();
        uf.union(1, 2);
        uf.union(2, 3);
        assert uf.isConnected(1, 3) : "Minimal preset should work";
        System.out.println("✓ testMinimalPreset passed");
    }

    private static void testBalancedPreset() {
        UnionFind uf = UnionFind.builder()
            .withSize(5)
            .withBalanced()
            .build();
        uf.union(1, 2);
        uf.union(2, 3);
        assert uf.isConnected(1, 3) : "Balanced preset should work";
        System.out.println("✓ testBalancedPreset passed");
    }

    private static void testPerformanceMetrics() {
        UnionFind uf = UnionFind.builder().withSize(10).build();
        uf.union(0, 1);
        uf.union(1, 2);
        uf.union(2, 3);
        uf.find(0);
        uf.find(3);
        PerformanceMetrics metrics = uf.getPerformanceMetrics();
        assert metrics.getFindCount() >= 2 : "Should track find operations";
        assert metrics.getUnionCount() == 3 : "Should track union operations";
        assert metrics.getTotalOperations() >= 5 : "Should track total operations";
        System.out.println("✓ testPerformanceMetrics passed (" + metrics + ")");
    }

    private static void testOperationListeners() {
        UnionFind uf = UnionFind.builder().withSize(5).build();

        class TestListener implements OperationListener {
            int findCalls = 0;
            int unionCalls = 0;

            @Override
            public void onFindOperation(int index, long totalFinds) {
                findCalls++;
            }

            @Override
            public void onUnionOperation(int index1, int index2, long totalUnions) {
                unionCalls++;
            }
        }

        TestListener listener = new TestListener();
        uf.addOperationListener(listener);

        uf.union(1, 2);
        uf.union(2, 3);
        uf.find(1);
        uf.find(3);

        assert listener.unionCalls == 2 : "Listener should record union operations";
        assert listener.findCalls >= 2 : "Listener should record find operations";

        uf.removeOperationListener(listener);
        listener.findCalls = 0;
        listener.unionCalls = 0;
        uf.union(3, 4);
        assert listener.unionCalls == 0 : "Removed listener should not be notified";

        System.out.println("✓ testOperationListeners passed");
    }

    private static void testExceptionHandling() {
        testInvalidIndexException();
        testInvalidSizeException();
    }

    private static void testInvalidIndexException() {
        try {
            UnionFind uf = UnionFind.builder().withSize(5).build();
            uf.find(-1);
            assert false : "Should throw InvalidIndexException";
        } catch (UnionFindException.InvalidIndexException e) {
            assert e.getMessage().contains("Invalid index") : "Exception message should contain 'Invalid index'";
        }
    }

    private static void testInvalidSizeException() {
        try {
            UnionFind.builder().withSize(-5).build();
            assert false : "Should throw InvalidSizeException";
        } catch (UnionFindException.InvalidSizeException e) {
            assert e.getMessage().contains("must be positive") : "Exception message should contain 'must be positive'";
        }
    }

    private static void testSafeUnionFind() {
        testSafeFind();
        testSafeUnion();
        testSafeIsConnected();
    }

    private static void testSafeFind() {
        UnionFind uf = UnionFind.builder().withSize(5).build();
        SafeUnionFind safe = SafeUnionFind.wrap(uf);

        Result<Integer> validFind = safe.find(0);
        assert validFind.isSuccess() : "Valid find should succeed";
        assert validFind.getValue() == 0 : "Find should return correct root";

        Result<Integer> invalidFind = safe.find(10);
        assert invalidFind.isError() : "Invalid find should fail";
        assert invalidFind.getError() instanceof UnionFindException : "Error should be UnionFindException";

        System.out.println("✓ testSafeFind passed");
    }

    private static void testSafeUnion() {
        UnionFind uf = UnionFind.builder().withSize(5).build();
        SafeUnionFind safe = SafeUnionFind.wrap(uf);

        Result<Void> validUnion = safe.union(1, 2);
        assert validUnion.isSuccess() : "Valid union should succeed";

        Result<Void> invalidUnion = safe.union(-1, 5);
        assert invalidUnion.isError() : "Invalid union should fail";

        System.out.println("✓ testSafeUnion passed");
    }

    private static void testSafeIsConnected() {
        UnionFind uf = UnionFind.builder().withSize(5).build();
        uf.union(1, 2);
        SafeUnionFind safe = SafeUnionFind.wrap(uf);

        Result<Boolean> validCheck = safe.isConnected(1, 2);
        assert validCheck.isSuccess() : "Valid check should succeed";
        assert validCheck.getValue() : "1 and 2 should be connected";

        Result<Boolean> invalidCheck = safe.isConnected(1, 10);
        assert invalidCheck.isError() : "Invalid check should fail";

        System.out.println("✓ testSafeIsConnected passed");
    }
}
