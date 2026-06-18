public class StrategyFactory {

    public static UnionStrategy createUnionStrategy(UnionStrategyType type) {
        return switch (type) {
            case RANK_BASED -> new RankBasedUnionStrategy();
            case SIMPLE -> new SimpleUnionStrategy();
        };
    }

    public static FindStrategy createFindStrategy(FindStrategyType type) {
        return switch (type) {
            case PATH_COMPRESSION -> new PathCompressionFindStrategy();
            case SIMPLE -> new SimpleFindStrategy();
        };
    }

    public enum UnionStrategyType {
        RANK_BASED, SIMPLE
    }

    public enum FindStrategyType {
        PATH_COMPRESSION, SIMPLE
    }
}
