package disjointset;

public final class DisjointSets {
    private DisjointSets() {
    }

    public static DisjointSet create(int size) {
        return new ArrayDisjointSet(size);
    }
}
