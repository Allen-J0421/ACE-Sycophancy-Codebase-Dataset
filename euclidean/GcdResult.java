package euclidean;

sealed interface GcdResult permits GcdResult.Success, GcdResult.Failure {
    record Success(int value) implements GcdResult {}
    record Failure(String reason) implements GcdResult {}
}
