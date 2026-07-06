package euclidean;

final class GcdCommand {

    private final Operands operands;

    GcdCommand(Operands operands) {
        this.operands = operands;
    }

    int execute() {
        return EuclideanAlgorithm.gcd(operands.left(), operands.right());
    }
}
