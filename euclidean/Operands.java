package euclidean;

final class Operands {

    private final int left;
    private final int right;

    Operands(int left, int right) {
        this.left = left;
        this.right = right;
    }

    int left() {
        return left;
    }

    int right() {
        return right;
    }
}
