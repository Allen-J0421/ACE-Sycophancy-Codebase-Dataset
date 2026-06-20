package sorting;

final class SortScenario {
    private final String name;
    private final int[] input;
    private final int[] expected;

    SortScenario(String name, int[] input, int[] expected) {
        this.name = name;
        this.input = input.clone();
        this.expected = expected.clone();
    }

    String name() {
        return name;
    }

    int[] input() {
        return input.clone();
    }

    int[] expected() {
        return expected.clone();
    }
}
