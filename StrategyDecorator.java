/**
 * Base decorator for wrapping computation strategies with additional behavior.
 */
public abstract class StrategyDecorator implements ComputationStrategy {
    protected final ComputationStrategy delegate;

    public StrategyDecorator(ComputationStrategy delegate) {
        this.delegate = delegate;
    }

    @Override
    public String getName() {
        return delegate.getName() + " (decorated)";
    }
}
