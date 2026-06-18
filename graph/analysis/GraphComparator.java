package graph.analysis;

import graph.core.IGraph;

public class GraphComparator {
    public static boolean isIsomorphic(IGraph g1, IGraph g2) {
        if (g1.getVertexCount() != g2.getVertexCount()) {
            return false;
        }
        if (g1.getEdgeCount() != g2.getEdgeCount()) {
            return false;
        }

        int[] deg1 = new int[g1.getVertexCount()];
        int[] deg2 = new int[g2.getVertexCount()];

        for (int i = 0; i < g1.getVertexCount(); i++) {
            deg1[i] = g1.getDegree(i);
            deg2[i] = g2.getDegree(i);
        }

        java.util.Arrays.sort(deg1);
        java.util.Arrays.sort(deg2);

        return java.util.Arrays.equals(deg1, deg2);
    }

    public static boolean isEqual(IGraph g1, IGraph g2) {
        if (g1.getVertexCount() != g2.getVertexCount()) {
            return false;
        }
        if (g1.getEdgeCount() != g2.getEdgeCount()) {
            return false;
        }

        for (int u = 0; u < g1.getVertexCount(); u++) {
            for (int v = 0; v < g1.getVertexCount(); v++) {
                if (g1.hasEdge(u, v) != g2.hasEdge(u, v)) {
                    return false;
                }
            }
        }

        return true;
    }

    public static double jaccardSimilarity(IGraph g1, IGraph g2) {
        if (g1.getVertexCount() != g2.getVertexCount()) {
            throw new IllegalArgumentException("Graphs must have same vertex count");
        }

        int intersection = 0;
        int union = 0;

        for (int u = 0; u < g1.getVertexCount(); u++) {
            for (int v = u + 1; v < g1.getVertexCount(); v++) {
                boolean edge1 = g1.hasEdge(u, v);
                boolean edge2 = g2.hasEdge(u, v);

                if (edge1 || edge2) {
                    union++;
                    if (edge1 && edge2) {
                        intersection++;
                    }
                }
            }
        }

        return union == 0 ? 1.0 : (double) intersection / union;
    }
}
