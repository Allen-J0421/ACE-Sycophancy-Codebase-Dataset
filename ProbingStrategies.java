final class ProbingStrategies {
    private ProbingStrategies() {}

    public static ProbingStrategy linear() {
        return (hash1, hash2, attempt, capacity) -> (hash1 + attempt) % capacity;
    }

    public static ProbingStrategy quadratic() {
        return (hash1, hash2, attempt, capacity) -> (hash1 + attempt * attempt) % capacity;
    }

    public static ProbingStrategy doubleHashing() {
        return (hash1, hash2, attempt, capacity) -> (hash1 + attempt * hash2) % capacity;
    }
}
