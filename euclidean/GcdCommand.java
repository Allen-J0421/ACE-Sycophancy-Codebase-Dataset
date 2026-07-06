package euclidean;

final class GcdCommand implements Command<Result<Integer, GcdError>> {

    private final Operands operands;
    private final GcdProvider provider;

    GcdCommand(Operands operands, GcdProvider provider) {
        this.operands = operands;
        this.provider = provider;
    }

    @Override
    public Result<Integer, GcdError> execute() {
        return provider.compute(operands.left(), operands.right());
    }
}
