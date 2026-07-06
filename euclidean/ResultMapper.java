package euclidean;

@FunctionalInterface
interface ResultMapper<T, U> {
    U apply(T value);
}
