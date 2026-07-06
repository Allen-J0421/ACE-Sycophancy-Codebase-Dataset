interface DistanceMap {

    void set(int vertex, int distance);

    int get(int vertex);

    int size();

    int[] snapshot();
}
