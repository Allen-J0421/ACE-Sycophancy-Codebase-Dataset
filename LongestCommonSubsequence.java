import java.util.Objects;

public final class LongestCommonSubsequence {
    private LongestCommonSubsequence() {
    }

    public static int lcs(String first, String second) {
        LcsRequest request = LcsRequest.from(first, second);

        if (request.hasKnownResult()) {
            return request.knownResult();
        }

        return new LcsSolver(request).solve();
    }
}
