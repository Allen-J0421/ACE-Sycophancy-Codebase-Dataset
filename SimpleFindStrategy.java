public class SimpleFindStrategy implements FindStrategy {

    @Override
    public int find(int[] parent, int i) {
        if (parent[i] == i) {
            return i;
        }
        return find(parent, parent[i]);
    }

    @Override
    public void reset(int[] parent) {
        for (int i = 0; i < parent.length; i++) {
            parent[i] = i;
        }
    }
}
