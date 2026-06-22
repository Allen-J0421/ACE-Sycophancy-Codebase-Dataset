final class BTreeSamples {
    private static final BTreeScenario DEFAULT_SCENARIO = BTreeScenario.fromKeys(
            3,
            new int[] {10, 20, 5, 6, 12, 30, 7, 17},
            BTreeScenario.present(6),
            BTreeScenario.missing(15)
    );

    private static final BTreeScenario MEMBERSHIP_SCENARIO = BTreeScenario.fromKeys(
            2,
            new int[] {8, 9, 10, 11, 15, 20, 17},
            BTreeScenario.present(10),
            BTreeScenario.present(17),
            BTreeScenario.missing(4)
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
