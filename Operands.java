import java.math.BigInteger;

record Operands(BigInteger first, BigInteger second) {
    Operands {
        first = requireOperand(first, "first");
        second = requireOperand(second, "second");
    }

    private static BigInteger requireOperand(BigInteger operand, String name) {
        if (operand == null) {
            throw new IllegalArgumentException(name + " operand must not be null.");
        }

        return operand;
    }

    BigInteger gcd() {
        if (first.signum() == 0 && second.signum() == 0) {
            throw new IllegalArgumentException("At least one operand must be non-zero.");
        }

        return first.abs().gcd(second.abs());
    }
}
