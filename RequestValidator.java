import java.util.ArrayList;
import java.util.List;

public class RequestValidator {
    private final List<ValidationRule> rules = new ArrayList<>();

    public interface ValidationRule {
        ValidationError validate(GraphRequest request);
    }

    public static class ValidationError {
        public final String fieldName;
        public final String message;

        public ValidationError(String fieldName, String message) {
            this.fieldName = fieldName;
            this.message = message;
        }

        @Override
        public String toString() {
            return fieldName + ": " + message;
        }
    }

    public RequestValidator addRule(ValidationRule rule) {
        rules.add(rule);
        return this;
    }

    public ValidationResult validate(GraphRequest request) {
        List<ValidationError> errors = new ArrayList<>();

        for (ValidationRule rule : rules) {
            ValidationError error = rule.validate(request);
            if (error != null) {
                errors.add(error);
            }
        }

        return new ValidationResult(errors.isEmpty(), errors);
    }

    public static class ValidationResult {
        public final boolean valid;
        public final List<ValidationError> errors;

        public ValidationResult(boolean valid, List<ValidationError> errors) {
            this.valid = valid;
            this.errors = new ArrayList<>(errors);
        }

        @Override
        public String toString() {
            if (valid) return "VALID";
            return "INVALID: " + String.join(", ",
                errors.stream().map(Object::toString).toArray(String[]::new));
        }
    }

    public static RequestValidator createDefault() {
        return new RequestValidator()
            .addRule(req -> req.getOperationType() == null || req.getOperationType().isEmpty() ?
                new ValidationError("operationType", "Cannot be empty") : null)
            .addRule(req -> req.getClientId() == null || req.getClientId().isEmpty() ?
                new ValidationError("clientId", "Cannot be empty") : null)
            .addRule(req -> req.getParameters() == null ?
                new ValidationError("parameters", "Cannot be null") : null);
    }
}
