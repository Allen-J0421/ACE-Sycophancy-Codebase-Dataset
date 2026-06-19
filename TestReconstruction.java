class TestReconstruction {
    public static void main(String[] args) {
        String s1 = "AGGTAB";
        String s2 = "GXTXAYB";

        String lcs = LcsSequenceReconstructor.reconstructLcs(s1, s2);
        System.out.println("String 1: " + s1);
        System.out.println("String 2: " + s2);
        System.out.println("LCS String: " + lcs);
        System.out.println("LCS Length: " + lcs.length());
    }
}
