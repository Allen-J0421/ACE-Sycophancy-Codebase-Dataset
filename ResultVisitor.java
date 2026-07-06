import java.util.function.Function;

interface ResultVisitor<T> {

    T onDistances(Distances distances);

    T onNegativeCycle(NegativeCycle cycle);

    static <T> ResultVisitor<T> of(
            Function<Distances, T> onDistances,
            Function<NegativeCycle, T> onNegativeCycle) {
        return new ResultVisitor<T>() {
            @Override public T onDistances(Distances d)   { return onDistances.apply(d); }
            @Override public T onNegativeCycle(NegativeCycle nc) { return onNegativeCycle.apply(nc); }
        };
    }
}
