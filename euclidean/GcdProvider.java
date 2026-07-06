package euclidean;

@FunctionalInterface
interface GcdProvider {
    Result<Integer, GcdError> compute(int a, int b);
}
