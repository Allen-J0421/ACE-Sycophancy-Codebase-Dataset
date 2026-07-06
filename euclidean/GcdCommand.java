package euclidean;

final class GcdCommand implements Command<GcdResult> {

    private final Operands operands;
    private final GcdProvider provider;

    GcdCommand(Operands operands, GcdProvider provider) {
        this.operands = operands;
        this.provider = provider;
    }

    @Override
    public GcdResult execute() {
        return provider.compute(operands.left(), operands.right());
    }
}
