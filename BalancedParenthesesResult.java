public record BalancedParenthesesResult(boolean balanced, int errorPosition, String reason) {

    static BalancedParenthesesResult success() {
        return new BalancedParenthesesResult(true, -1, null);
    }

    static BalancedParenthesesResult failure(int position, String reason) {
        return new BalancedParenthesesResult(false, position, reason);
    }
}
