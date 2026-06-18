public class StrategyConfiguration {
    private StrategyFactory.UnionStrategyType unionType;
    private StrategyFactory.FindStrategyType findType;

    public StrategyConfiguration(StrategyFactory.UnionStrategyType unionType, StrategyFactory.FindStrategyType findType) {
        this.unionType = unionType;
        this.findType = findType;
    }

    public UnionStrategy getUnionStrategy() {
        return StrategyFactory.createUnionStrategy(unionType);
    }

    public FindStrategy getFindStrategy() {
        return StrategyFactory.createFindStrategy(findType);
    }

    public static class Presets {
        public static StrategyConfiguration optimal() {
            return new StrategyConfiguration(
                StrategyFactory.UnionStrategyType.RANK_BASED,
                StrategyFactory.FindStrategyType.PATH_COMPRESSION
            );
        }

        public static StrategyConfiguration minimal() {
            return new StrategyConfiguration(
                StrategyFactory.UnionStrategyType.SIMPLE,
                StrategyFactory.FindStrategyType.SIMPLE
            );
        }

        public static StrategyConfiguration balanced() {
            return new StrategyConfiguration(
                StrategyFactory.UnionStrategyType.RANK_BASED,
                StrategyFactory.FindStrategyType.SIMPLE
            );
        }
    }
}
