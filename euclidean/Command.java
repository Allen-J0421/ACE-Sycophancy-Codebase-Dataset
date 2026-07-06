package euclidean;

@FunctionalInterface
interface Command<T> {
    T execute();
}
