/**
 * Base exception for LCS operations.
 * All LCS-specific exceptions inherit from this for type-safe error handling.
 */
class LcsException extends RuntimeException {
    /**
     * Constructs an LcsException with the specified message.
     *
     * @param message the error message
     */
    LcsException(String message) {
        super(message);
    }

    /**
     * Constructs an LcsException with the specified message and cause.
     *
     * @param message the error message
     * @param cause   the underlying cause
     */
    LcsException(String message, Throwable cause) {
        super(message, cause);
    }
}

/**
 * Exception thrown when LCS input validation fails.
 * Raised when string arguments don't meet requirements (null, invalid length, etc.).
 */
class InvalidInputException extends LcsException {
    /**
     * Constructs an InvalidInputException for null or invalid input.
     *
     * @param message description of the validation failure
     */
    InvalidInputException(String message) {
        super(message);
    }
}

/**
 * Exception thrown when LCS solver parameters are invalid.
 * Raised when solver configuration is inconsistent or unsupported.
 */
class InvalidLcsParametersException extends LcsException {
    /**
     * Constructs an InvalidLcsParametersException.
     *
     * @param message description of the parameter issue
     */
    InvalidLcsParametersException(String message) {
        super(message);
    }
}

/**
 * Exception thrown when caching operations fail.
 * Raised when cache is corrupted, eviction fails, or cache bounds exceeded.
 */
class CacheException extends LcsException {
    /**
     * Constructs a CacheException.
     *
     * @param message description of the caching error
     */
    CacheException(String message) {
        super(message);
    }

    /**
     * Constructs a CacheException with underlying cause.
     *
     * @param message description of the caching error
     * @param cause   the underlying exception
     */
    CacheException(String message, Throwable cause) {
        super(message, cause);
    }
}
