import java.util.List;

/**
 * Decorator strategy that validates input before delegating to wrapped strategy.
 */
public class ValidationStrategy extends StrategyDecorator {
    public ValidationStrategy(ComputationStrategy delegate) {
        super(delegate);
    }

    @Override
    public List<Long> compute(int[] arr) {
        InputValidator.validate(arr);
        return delegate.compute(arr);
    }

    @Override
    public String getName() {
        return "Validating " + delegate.getName();
    }
}
