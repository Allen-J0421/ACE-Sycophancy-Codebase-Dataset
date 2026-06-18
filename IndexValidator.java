public class IndexValidator {
    private int size;

    public IndexValidator(int size) {
        validateSize(size);
        this.size = size;
    }

    public void validate(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(
                String.format("Index %d out of bounds [0, %d)", index, size)
            );
        }
    }

    public int getSize() {
        return size;
    }

    private void validateSize(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Size must be positive");
        }
    }
}
