class DoubleHashingStrategy implements ProbingStrategy {
    @Override
    public int probe(int hash1, int hash2, int attempt, int capacity) {
        return (hash1 + attempt * hash2) % capacity;
    }
}
