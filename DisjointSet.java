public interface DisjointSet {
    int find(int element);

    boolean union(int first, int second);

    boolean connected(int first, int second);

    int size();

    int componentCount();

    default int components() {
        return componentCount();
    }

    int componentSize(int element);
}
