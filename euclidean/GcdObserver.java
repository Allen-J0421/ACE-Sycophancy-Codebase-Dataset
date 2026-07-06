package euclidean;

@FunctionalInterface
interface GcdObserver {
    void onCompute(int a, int b, GcdResult result);
}
