package com.example.dfs;

import java.util.ArrayList;
import java.util.List;

public final class DepthFirstSearchDemo {

    private static final int VERTEX_COUNT = 6;
    private static final int[][] SAMPLE_EDGES = {
            {1, 2},
            {0, 3},
            {2, 0},
            {5, 4}
    };

    private DepthFirstSearchDemo() {
    }

    private static List<List<Integer>> buildSampleGraph() {
        List<List<Integer>> adjacencyList = createGraph(VERTEX_COUNT);

        for (int[] edge : SAMPLE_EDGES) {
            addUndirectedEdge(adjacencyList, edge[0], edge[1]);
        }

        return adjacencyList;
    }

    private static List<List<Integer>> createGraph(int vertexCount) {
        List<List<Integer>> adjacencyList = new ArrayList<>(vertexCount);

        for (int vertex = 0; vertex < vertexCount; vertex++) {
            adjacencyList.add(new ArrayList<>());
        }

        return adjacencyList;
    }

    private static void addUndirectedEdge(List<List<Integer>> adjacencyList, int source, int destination) {
        adjacencyList.get(source).add(destination);
        adjacencyList.get(destination).add(source);
    }

    private static void printTraversal(List<Integer> traversalOrder) {
        for (int vertex : traversalOrder) {
            System.out.print(vertex + " ");
        }
    }

    public static void main(String[] args) {
        List<List<Integer>> adjacencyList = buildSampleGraph();
        List<Integer> traversalOrder = DepthFirstSearch.dfs(adjacencyList);

        printTraversal(traversalOrder);
    }
}
