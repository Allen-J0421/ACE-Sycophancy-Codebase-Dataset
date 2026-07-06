interface ProbingStrategy {
    /**
     * Returns the table index to probe on the given attempt.
     *
     * @param hash1   primary hash of the key (index of first probe)
     * @param hash2   secondary hash of the key (step size for double hashing)
     * @param attempt probe number, starting at 0
     * @param capacity current table capacity
     */
    int probe(int hash1, int hash2, int attempt, int capacity);
}
