import java.util.Arrays;

final class BTreeSamples {
    private static final BTreeScenario DEFAULT_SCENARIO = new BTreeScenario(
            3,
            new int[] {10, 20, 5, 6, 12, 30, 7, 17},
            Arrays.asList(5, 6, 7, 10, 12, 17, 20, 30),
            new int[] {6},
            new int[] {15}
    );

    private static final BTreeScenario MEMBERSHIP_SCENARIO = new BTreeScenario(
            2,
            new int[] {8, 9, 10, 11, 15, 20, 17},
            Arrays.asList(8, 9, 10, 11, 15, 17, 20),
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
