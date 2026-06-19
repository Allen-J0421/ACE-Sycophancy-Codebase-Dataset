/**
 * Validation framework for LCS inputs.
 * Enforces constraints and provides detailed validation feedback.
 */
interface LcsValidator {
    /**
     * Validates input and returns result with feedback.
     */
    ValidationResult validate(String s1, String s2);
}

/**
 * Validation result containing status and detailed messages.
 */
class ValidationResult {
    final boolean valid;
    final String message;
    final ValidationLevel level;

    enum ValidationLevel {
        PASS("Input passes all validation checks"),
        WARNING("Input has issues but can be processed"),
        ERROR("Input fails validation");

        final String description;

        ValidationLevel(String description) {
            this.description = description;
        }
    }

    ValidationResult(boolean valid, String message, ValidationLevel level) {
        this.valid = valid;
        this.message = message;
        this.level = level;
    }

    static ValidationResult pass(String msg) {
        return new ValidationResult(true, msg, ValidationLevel.PASS);
    }

    static ValidationResult warning(String msg) {
        return new ValidationResult(true, msg, ValidationLevel.WARNING);
    }

    static ValidationResult error(String msg) {
        return new ValidationResult(false, msg, ValidationLevel.ERROR);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s", level.name(), message);
    }
}

/**
 * Standard validation rules.
 */
class StandardLcsValidator implements LcsValidator {

    static final int MAX_LENGTH = 10_000;
    static final int WARN_LENGTH = 5_000;

    @Override
    public ValidationResult validate(String s1, String s2) {
        // Null checks
        if (s1 == null || s2 == null) {
            return ValidationResult.error("Null input strings");
        }

        // Length checks
        if (s1.length() == 0 || s2.length() == 0) {
            return ValidationResult.pass("One or both strings are empty (LCS length = 0)");
        }

        if (s1.length() > MAX_LENGTH || s2.length() > MAX_LENGTH) {
            return ValidationResult.error(
                    String.format("String exceeds max length %d (got %d, %d)",
                            MAX_LENGTH, s1.length(), s2.length())
            );
        }

        if (s1.length() > WARN_LENGTH || s2.length() > WARN_LENGTH) {
            return ValidationResult.warning(
                    String.format("Large input (%d, %d chars) - consider approximation",
                            s1.length(), s2.length())
            );
        }

        return ValidationResult.pass(String.format("Valid input (%d, %d chars)",
                s1.length(), s2.length()));
    }
}

/**
 * Strict validation with encoding checks.
 */
class StrictLcsValidator implements LcsValidator {

    @Override
    public ValidationResult validate(String s1, String s2) {
        // First pass base validation
        ValidationResult base = new StandardLcsValidator().validate(s1, s2);
        if (!base.valid) {
            return base;
        }

        // Encoding checks
        if (!isValidUnicode(s1) || !isValidUnicode(s2)) {
            return ValidationResult.error("Invalid Unicode in input");
        }

        // Character class consistency
        boolean s1Homogeneous = isCharacterClassHomogeneous(s1);
        boolean s2Homogeneous = isCharacterClassHomogeneous(s2);

        if (!s1Homogeneous || !s2Homogeneous) {
            return ValidationResult.warning("Mixed character classes (letters, digits, symbols)");
        }

        return ValidationResult.pass("Input passes strict validation");
    }

    private boolean isValidUnicode(String s) {
        return s != null && s.matches("^[\\p{L}\\p{N}\\p{P}\\p{S}\\p{Z}]*$");
    }

    private boolean isCharacterClassHomogeneous(String s) {
        if (s.isEmpty()) return true;

        boolean hasLetters = s.matches(".*[a-zA-Z].*");
        boolean hasDigits = s.matches(".*[0-9].*");
        boolean hasSymbols = s.matches(".*[^a-zA-Z0-9\\s].*");

        int classCount = (hasLetters ? 1 : 0) + (hasDigits ? 1 : 0) + (hasSymbols ? 1 : 0);
        return classCount <= 1;
    }
}

/**
 * Lenient validation (minimal checks).
 */
class LenientLcsValidator implements LcsValidator {

    @Override
    public ValidationResult validate(String s1, String s2) {
        if (s1 == null || s2 == null) {
            return ValidationResult.error("Null input");
        }
        return ValidationResult.pass("Input accepted");
    }
}

/**
 * Composite validator combining multiple rules.
 */
class CompositeValidator implements LcsValidator {
    final LcsValidator[] validators;

    CompositeValidator(LcsValidator... validators) {
        this.validators = validators;
    }

    @Override
    public ValidationResult validate(String s1, String s2) {
        for (LcsValidator v : validators) {
            ValidationResult result = v.validate(s1, s2);
            if (!result.valid) {
                return result;
            }
        }
        return ValidationResult.pass("All validators passed");
    }

    static class Builder {
        LcsValidator[] validators = new LcsValidator[0];

        Builder add(LcsValidator v) {
            LcsValidator[] newValidators = new LcsValidator[validators.length + 1];
            System.arraycopy(validators, 0, newValidators, 0, validators.length);
            newValidators[validators.length] = v;
            this.validators = newValidators;
            return this;
        }

        CompositeValidator build() {
            return new CompositeValidator(validators);
        }
    }
}

/**
 * Factory for validator instances.
 */
class LcsValidators {
    static StandardLcsValidator standard() {
        return new StandardLcsValidator();
    }

    static StrictLcsValidator strict() {
        return new StrictLcsValidator();
    }

    static LenientLcsValidator lenient() {
        return new LenientLcsValidator();
    }

    static LcsValidator composite(LcsValidator... validators) {
        return new CompositeValidator(validators);
    }

    static CompositeValidator.Builder compositeBuilder() {
        return new CompositeValidator.Builder();
    }
}
