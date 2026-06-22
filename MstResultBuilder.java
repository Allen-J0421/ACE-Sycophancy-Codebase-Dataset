final class MstResultBuilder {
    private MstResult result = MstResult.empty();

    void include(Edge edge) {
        result = result.include(edge);
    }

    int edgesUsed() {
        return result.edgesUsed();
    }

    MstResult build() {
        return result;
    }
}
