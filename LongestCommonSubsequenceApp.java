public class LongestCommonSubsequenceApp {

    public static void main(String[] args) {
        if (args.length != 0 && args.length != 2) {
            System.err.println("Usage: LongestCommonSubsequenceApp <string1> <string2>");
            System.exit(1);
        }
        String s1 = args.length == 2 ? args[0] : "AGGTAB";
        String s2 = args.length == 2 ? args[1] : "GXTXAYB";
        LcsSolver solver = new LongestCommonSubsequence();
        LcsResult result = solver.compute(new LcsInput(s1, s2));
        System.out.println(result);
    }
}
