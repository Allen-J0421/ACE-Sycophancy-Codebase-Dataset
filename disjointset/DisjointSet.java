package disjointset;

public interface DisjointSet {
    static DisjointSet create(int size) {
        return new ArrayDisjointSet(size);
    }

    int find(int element);

    boolean union(int first, int second);

    boolean connected(int first, int second);

    int componentSize(int element);

    int components();

    int size();

    default boolean isEmpty() {
        return size() == 0;
    }
}
