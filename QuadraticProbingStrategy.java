class QuadraticProbingStrategy implements ProbingStrategy {
    @Override
    public int probe(int hash1, int hash2, int attempt, int capacity) {
        return (hash1 + attempt * attempt) % capacity;
    }
}
