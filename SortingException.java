/**
 * Custom exception for sorting-related errors.
 * Provides detailed error context for debugging and error handling.
 */
class SortingException extends RuntimeException {
    public enum ErrorType {
        INVALID_CONFIGURATION("Configuration parameters are invalid"),
        INVALID_ARRAY("Array parameter is invalid or null"),
        INVALID_BOUNDS("Array bounds are out of range"),
        INTERNAL_ERROR("An internal error occurred during sorting");

        private final String message;

        ErrorType(String message) {
            this.message = message;
        }

        public String getDefaultMessage() {
            return message;
        }
    }

    private final ErrorType errorType;
    private final String detail;

    public SortingException(ErrorType errorType) {
        super(errorType.getDefaultMessage());
        this.errorType = errorType;
        this.detail = null;
    }

    public SortingException(ErrorType errorType, String detail) {
        super(errorType.getDefaultMessage() + ": " + detail);
        this.errorType = errorType;
        this.detail = detail;
    }

    public SortingException(ErrorType errorType, String detail, Throwable cause) {
        super(errorType.getDefaultMessage() + ": " + detail, cause);
        this.errorType = errorType;
        this.detail = detail;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public String getDetail() {
        return detail;
    }
}
