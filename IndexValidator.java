public class IndexValidator {
    private int size;

    public IndexValidator(int size) {
        validateSize(size);
        this.size = size;
    }

    public void validate(int index) {
        if (index < 0 || index >= size) {
            throw new UnionFindException.InvalidIndexException(index, size);
        }
    }

    public Result<Integer> validateSafe(int index) {
        if (index < 0 || index >= size) {
            return Result.error(new UnionFindException.InvalidIndexException(index, size));
        }
        return Result.ok(index);
    }

    public int getSize() {
        return size;
    }

    private void validateSize(int size) {
        if (size <= 0) {
            throw new UnionFindException.InvalidSizeException(size);
        }
    }
}
