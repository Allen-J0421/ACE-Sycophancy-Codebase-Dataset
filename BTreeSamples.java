final class BTreeSamples {
    private static final BTreeScenario DEFAULT_SCENARIO = BTreeScenario.fromKeys(
            3,
            new int[] {10, 20, 5, 6, 12, 30, 7, 17},
            new int[] {6},
            new int[] {15}
    );

    private static final BTreeScenario MEMBERSHIP_SCENARIO = BTreeScenario.fromKeys(
            2,
            new int[] {8, 9, 10, 11, 15, 20, 17},
            new int[] {10, 17},
            new int[] {4}
    );

    private BTreeSamples() {
    }

    static BTreeScenario defaultScenario() {
        return DEFAULT_SCENARIO;
    }

    static BTreeScenario membershipScenario() {
        return MEMBERSHIP_SCENARIO;
    }
}
