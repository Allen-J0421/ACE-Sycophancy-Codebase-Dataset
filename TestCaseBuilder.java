import java.util.ArrayList;
import java.util.List;

class TestCaseBuilder<T extends Comparable<T>> {

    private final Sorter<T> sorter;
    private final Reporter reporter;
    private final List<SortTestCase> cases = new ArrayList<>();

    TestCaseBuilder(Sorter<T> sorter, Reporter reporter) {
        this.sorter = sorter;
        this.reporter = reporter;
    }

    TestCaseBuilder<T> add(String label, T[] input) {
        cases.add(SortTestCase.of(label, input, sorter, reporter));
        return this;
    }

    void registerAll(TestRegistry registry) {
        cases.forEach(registry::register);
    }
}
