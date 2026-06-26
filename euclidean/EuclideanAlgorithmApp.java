package euclidean;

public final class EuclideanAlgorithmApp {

    private EuclideanAlgorithmApp() {
        // Utility class.
    }

    public static void main(String[] args) {
        Operands operands = ArgumentParser.parseOperands(args);
        System.out.println(EuclideanAlgorithm.gcd(operands.left(), operands.right()));
    }
}
