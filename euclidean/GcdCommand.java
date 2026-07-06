package euclidean;

final class GcdCommand implements Command<Integer> {

    private final Operands operands;
    private final GcdProvider provider;

    GcdCommand(Operands operands, GcdProvider provider) {
        this.operands = operands;
        this.provider = provider;
    }

    @Override
    public Integer execute() {
        return provider.compute(operands.left(), operands.right());
    }
}
