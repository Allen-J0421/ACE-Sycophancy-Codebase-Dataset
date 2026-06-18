public class PathCompressionFindStrategy implements FindStrategy {

    @Override
    public int find(int[] parent, int i) {
        if (parent[i] != i) {
            parent[i] = find(parent, parent[i]);
        }
        return parent[i];
    }

    @Override
    public void reset(int[] parent) {
        for (int i = 0; i < parent.length; i++) {
            parent[i] = i;
        }
    }
}
