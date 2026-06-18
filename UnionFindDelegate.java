public interface UnionFindDelegate {
    int find(int i);
    void union(int i, int j);
    boolean isConnected(int i, int j);
}
