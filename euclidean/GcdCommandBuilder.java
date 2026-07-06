package euclidean;

final class GcdCommandBuilder {

    private GcdProvider provider = GcdProviderRegistry.getDefault();
    private GcdObserver observer;

    GcdCommandBuilder provider(String name) {
        this.provider = GcdProviderRegistry.get(name);
        return this;
    }

    GcdCommandBuilder provider(GcdProvider provider) {
        this.provider = provider;
        return this;
    }

    GcdCommandBuilder observer(GcdObserver observer) {
        this.observer = observer;
        return this;
    }

    GcdCommand build(Operands operands) {
        GcdProvider effective = observer == null
                ? provider
                : new LoggingGcdProvider(provider, observer);
        return new GcdCommand(operands, effective);
    }
}
