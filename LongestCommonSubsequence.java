import java.util.Objects;

public final class LongestCommonSubsequence {
    private LongestCommonSubsequence() {
    }

    public static int lcs(String first, String second) {
        LcsInput input = LcsInput.from(first, second);

        if (input.hasKnownResult()) {
            return input.knownResult();
        }

        return LcsSolver.solve(input.normalizedSequences());
    }
}
