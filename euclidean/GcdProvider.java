package euclidean;

@FunctionalInterface
interface GcdProvider {
    GcdResult compute(int a, int b);
}
