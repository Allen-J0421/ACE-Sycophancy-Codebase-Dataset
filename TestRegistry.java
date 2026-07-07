import java.util.ArrayList;
import java.util.List;

class TestRegistry {

    private final List<SortTestCase> cases = new ArrayList<>();

    void register(SortTestCase testCase) {
        cases.add(testCase);
    }

    void runAll() {
        cases.forEach(SortTestCase::run);
    }
}
