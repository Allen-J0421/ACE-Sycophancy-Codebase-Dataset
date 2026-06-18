interface Validator<T> {
    void validate(T value) throws IllegalArgumentException;
}
