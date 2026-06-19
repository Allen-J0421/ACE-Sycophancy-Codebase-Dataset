/**
 * Strategy interface for input normalization/preprocessing.
 * Enables composable text transformations before LCS computation.
 * Examples: case conversion, whitespace handling, unicode normalization.
 */
interface InputNormalizer {
    /**
     * Normalizes a string according to this strategy.
     *
     * @param input the string to normalize
     * @return normalized string
     */
    String normalize(String input);

    /**
     * Chains this normalizer with another.
     * Creates a composite normalizer that applies both transformations.
     *
     * @param next the normalizer to apply after this one
     * @return composite normalizer
     */
    default InputNormalizer andThen(InputNormalizer next) {
        return input -> next.normalize(normalize(input));
    }
}

/**
 * No-op normalizer: returns input unchanged.
 * Useful as a default or base case in composition.
 */
class IdentityNormalizer implements InputNormalizer {
    @Override
    public String normalize(String input) {
        return input;
    }
}

/**
 * Case normalization: converts to lowercase.
 * Useful for case-insensitive LCS comparison.
 */
class LowercaseNormalizer implements InputNormalizer {
    @Override
    public String normalize(String input) {
        return input.toLowerCase();
    }
}

/**
 * Case normalization: converts to uppercase.
 * Alternative to lowercase for uppercase text.
 */
class UppercaseNormalizer implements InputNormalizer {
    @Override
    public String normalize(String input) {
        return input.toUpperCase();
    }
}

/**
 * Whitespace trimming: removes leading and trailing whitespace.
 */
class TrimNormalizer implements InputNormalizer {
    @Override
    public String normalize(String input) {
        return input.trim();
    }
}

/**
 * Whitespace collapsing: reduces consecutive whitespace to single space.
 * Also trims leading/trailing whitespace.
 */
class CollapseWhitespaceNormalizer implements InputNormalizer {
    @Override
    public String normalize(String input) {
        return input.trim().replaceAll("\\s+", " ");
    }
}

/**
 * Whitespace removal: deletes all whitespace characters.
 * Useful for comparing text ignoring formatting.
 */
class RemoveWhitespaceNormalizer implements InputNormalizer {
    @Override
    public String normalize(String input) {
        return input.replaceAll("\\s+", "");
    }
}

/**
 * Punctuation removal: deletes common punctuation marks.
 * Useful for comparing text content while ignoring punctuation.
 */
class RemovePunctuationNormalizer implements InputNormalizer {
    @Override
    public String normalize(String input) {
        return input.replaceAll("[^\\p{L}\\p{N}\\s]", "");
    }
}

/**
 * Digit removal: deletes all numeric digits.
 * Useful for comparing text-only content.
 */
class RemoveDigitsNormalizer implements InputNormalizer {
    @Override
    public String normalize(String input) {
        return input.replaceAll("[0-9]", "");
    }
}

/**
 * Composite normalizer: applies multiple normalizers in sequence.
 * Enables complex preprocessing pipelines.
 * Example: lowercase → trim → collapse whitespace
 */
class CompositeNormalizer implements InputNormalizer {
    private final InputNormalizer[] normalizers;

    /**
     * Creates a composite normalizer from a list of normalizers.
     *
     * @param normalizers normalizers to apply in order
     */
    CompositeNormalizer(InputNormalizer... normalizers) {
        this.normalizers = normalizers;
    }

    @Override
    public String normalize(String input) {
        String result = input;
        for (InputNormalizer normalizer : normalizers) {
            result = normalizer.normalize(result);
        }
        return result;
    }

    @Override
    public String toString() {
        return "CompositeNormalizer(" + normalizers.length + " steps)";
    }
}

/**
 * Preset normalizer configurations for common use cases.
 */
class InputNormalizers {
    /**
     * No normalization: identity function.
     */
    static InputNormalizer none() {
        return new IdentityNormalizer();
    }

    /**
     * Case-insensitive: lowercase only.
     */
    static InputNormalizer caseInsensitive() {
        return new LowercaseNormalizer();
    }

    /**
     * Whitespace-insensitive: collapse all whitespace to single space.
     */
    static InputNormalizer whitespaceInsensitive() {
        return new CollapseWhitespaceNormalizer();
    }

    /**
     * Text-only: case-insensitive, remove whitespace and punctuation.
     * Useful for comparing pure text content.
     */
    static InputNormalizer textOnly() {
        return new LowercaseNormalizer()
                .andThen(new RemoveWhitespaceNormalizer())
                .andThen(new RemovePunctuationNormalizer());
    }

    /**
     * Alphanumeric-only: remove whitespace, punctuation, but keep digits.
     */
    static InputNormalizer alphanumericOnly() {
        return new LowercaseNormalizer()
                .andThen(new RemoveWhitespaceNormalizer())
                .andThen(new RemovePunctuationNormalizer());
    }

    /**
     * Numbers-only: remove all non-numeric characters.
     */
    static InputNormalizer numbersOnly() {
        return input -> input.replaceAll("[^0-9]", "");
    }

    /**
     * Strict comparison: case-insensitive and collapse whitespace.
     * Good default for general text comparison.
     */
    static InputNormalizer strictComparison() {
        return new LowercaseNormalizer()
                .andThen(new CollapseWhitespaceNormalizer());
    }

    /**
     * Custom composite: chain multiple normalizers.
     *
     * @param normalizers normalizers to apply in order
     * @return composite normalizer
     */
    static InputNormalizer compose(InputNormalizer... normalizers) {
        return new CompositeNormalizer(normalizers);
    }

    /**
     * Builder for custom normalizer configurations.
     */
    static class Builder {
        private InputNormalizer normalizer = new IdentityNormalizer();

        /**
         * Adds lowercase transformation.
         */
        Builder lowercase() {
            normalizer = normalizer.andThen(new LowercaseNormalizer());
            return this;
        }

        /**
         * Adds trim transformation.
         */
        Builder trim() {
            normalizer = normalizer.andThen(new TrimNormalizer());
            return this;
        }

        /**
         * Adds collapse-whitespace transformation.
         */
        Builder collapseWhitespace() {
            normalizer = normalizer.andThen(new CollapseWhitespaceNormalizer());
            return this;
        }

        /**
         * Adds remove-whitespace transformation.
         */
        Builder removeWhitespace() {
            normalizer = normalizer.andThen(new RemoveWhitespaceNormalizer());
            return this;
        }

        /**
         * Adds remove-punctuation transformation.
         */
        Builder removePunctuation() {
            normalizer = normalizer.andThen(new RemovePunctuationNormalizer());
            return this;
        }

        /**
         * Adds custom normalizer to chain.
         */
        Builder add(InputNormalizer n) {
            normalizer = normalizer.andThen(n);
            return this;
        }

        /**
         * Builds the composed normalizer.
         */
        InputNormalizer build() {
            return normalizer;
        }
    }

    /**
     * Creates a builder for custom configurations.
     */
    static Builder builder() {
        return new Builder();
    }
}
