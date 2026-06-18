public class UnionFindException extends RuntimeException {
    public UnionFindException(String message) {
        super(message);
    }

    public UnionFindException(String message, Throwable cause) {
        super(message, cause);
    }

    public static class InvalidIndexException extends UnionFindException {
        public InvalidIndexException(int index, int size) {
            super(String.format("Invalid index %d for UnionFind of size %d", index, size));
        }
    }

    public static class InvalidSizeException extends UnionFindException {
        public InvalidSizeException(int size) {
            super(String.format("UnionFind size must be positive, got %d", size));
        }
    }

    public static class InvalidOperationException extends UnionFindException {
        public InvalidOperationException(String operation) {
            super("Invalid operation: " + operation);
        }
    }
}
