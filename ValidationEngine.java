import java.util.ArrayList;
import java.util.List;

public class ValidationEngine {
  private final List<ValidationRule> rules;

  public ValidationEngine() {
    this.rules = new ArrayList<>();
  }

  public void addRule(ValidationRule rule) {
    rules.add(rule);
  }

  public boolean validate(int[] coins, int targetSum) {
    for (ValidationRule rule : rules) {
      if (!rule.validate(coins, targetSum)) {
        return false;
      }
    }
    return true;
  }

  public List<String> getFailedRules(int[] coins, int targetSum) {
    List<String> failed = new ArrayList<>();
    for (ValidationRule rule : rules) {
      if (!rule.validate(coins, targetSum)) {
        failed.add(rule.getRuleName() + ": " + rule.getErrorMessage());
      }
    }
    return failed;
  }

  public String generateValidationReport(int[] coins, int targetSum) {
    StringBuilder sb = new StringBuilder();
    sb.append("=== Validation Report ===\n");
    sb.append("Total Rules: ").append(rules.size()).append("\n\n");

    List<String> failed = getFailedRules(coins, targetSum);
    if (failed.isEmpty()) {
      sb.append("✓ All validations passed\n");
    } else {
      sb.append("✗ Failed validations:\n");
      for (String failure : failed) {
        sb.append("  - ").append(failure).append("\n");
      }
    }

    return sb.toString();
  }

  @Override
  public String toString() {
    return String.format("ValidationEngine{rules=%d}", rules.size());
  }
}
