interface ShortestPathResult {

    <T> T accept(ResultVisitor<T> visitor);
}
