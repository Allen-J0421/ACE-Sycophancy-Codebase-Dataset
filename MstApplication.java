class MstApplication {

    public static void main(String[] args) {
        int[][] matrix = {
            { 0, 2, 0, 6, 0 },
            { 2, 0, 3, 8, 5 },
            { 0, 3, 0, 0, 7 },
            { 6, 8, 0, 0, 9 },
            { 0, 5, 7, 9, 0 }
        };

        MstAlgorithm algorithm = new PrimsMST();
        MstFormatter formatter = new TabularMstFormatter();
        MstResult result = algorithm.computeMST(Graph.fromMatrix(matrix));
        System.out.println(formatter.format(result));
    }
}
