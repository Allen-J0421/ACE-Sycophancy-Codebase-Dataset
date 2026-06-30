enum SearchType {
    BINARY,
    LINEAR;

    static SearchType from(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("search type must not be blank");
        }

        for (SearchType searchType : values()) {
            if (searchType.name().equalsIgnoreCase(value.trim())) {
                return searchType;
            }
        }

        throw new IllegalArgumentException("Unsupported search type: " + value);
    }
}
