final class SampleNetworks {
    static final int SIX_VERTEX_SOURCE = 0;
    static final int SIX_VERTEX_SINK = 5;
    static final int SIX_VERTEX_MAX_FLOW = 23;

    private SampleNetworks() {
    }

    static int[][] sixVertexNetwork() {
        return new int[][] {
            { 0, 16, 13, 0, 0, 0 },
            { 0, 0, 10, 12, 0, 0 },
            { 0, 4, 0, 0, 14, 0 },
            { 0, 0, 9, 0, 0, 20 },
            { 0, 0, 0, 7, 0, 4 },
            { 0, 0, 0, 0, 0, 0 }
        };
    }
}
