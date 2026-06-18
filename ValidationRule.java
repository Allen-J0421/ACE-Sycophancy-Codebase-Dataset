public interface ValidationRule {
  boolean validate(int[] coins, int targetSum);
  String getErrorMessage();
  String getRuleName();
}
