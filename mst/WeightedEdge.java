package mst;

public record WeightedEdge<W>(int from, int to, W weight) {
    @Override
    public String toString() {
        return from + " - " + to + "\t" + weight;
    }
}
