import java.util.Objects;

record KnapsackSolution(Problem problem, int optimalValue) {
    KnapsackSolution {
        Objects.requireNonNull(problem, "problem must not be null");
        Validation.requireNonNegative(optimalValue, "optimalValue");
    }
}
