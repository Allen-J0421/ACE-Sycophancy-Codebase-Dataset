package euclidean;

final class GcdCommand implements Command<Integer> {

    private final Operands operands;

    GcdCommand(Operands operands) {
        this.operands = operands;
    }

    @Override
    public Integer execute() {
        return EuclideanAlgorithm.gcd(operands.left(), operands.right());
    }
}
