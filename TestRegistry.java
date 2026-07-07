import java.util.ArrayList;
import java.util.List;

class TestRegistry {

    private final List<SortTestCase> cases = new ArrayList<>();

    void register(SortTestCase testCase) {
        cases.add(testCase);
    }

    void forEach(TestCaseConsumer consumer) {
        for (SortTestCase testCase : cases) {
            consumer.accept(testCase);
        }
    }
}
