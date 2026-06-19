public class LongestCommonSubsequenceApp {

    public static void main(String[] args) {
        LongestCommonSubsequence solver = new LongestCommonSubsequence();
        LcsInput input = new LcsInput("AGGTAB", "GXTXAYB");
        LcsResult result = solver.compute(input);
        System.out.println(result);
    }
}
