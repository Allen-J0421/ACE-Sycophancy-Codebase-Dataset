class PartitionState {

    enum Color {
        RED, BLUE;

        Color opposite() {
            return this == RED ? BLUE : RED;
        }
    }

    private final Color[] colors;

    PartitionState(int V) {
        colors = new Color[V];
    }

    void setColor(int v, Color c) {
        colors[v] = c;
    }

    Color getColor(int v) {
        return colors[v];
    }

    boolean isUncolored(int v) {
        return colors[v] == null;
    }

    Partition buildPartition() {
        Partition result = new Partition(true);
        for (int i = 0; i < colors.length; i++) {
            if (colors[i] == Color.RED) {
                result.setA.add(i);
            } else {
                result.setB.add(i);
            }
        }
        return result;
    }
}
