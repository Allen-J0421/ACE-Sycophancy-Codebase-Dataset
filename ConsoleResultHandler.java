class ConsoleResultHandler implements ResultHandler {
    @Override
    public void handle(SearchResult result) {
        if (result == null) {
            throw new IllegalArgumentException("result must not be null");
        }

        if (!result.isFound()) {
            System.out.println("Element is not present in array");
        } else {
            System.out.println("Element is present at index " + result.index());
        }
    }
}
