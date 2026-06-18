record Item(int weight, int value) {
    Item {
        Validation.requireNonNegative(weight, "weight");
        Validation.requireNonNegative(value, "value");
    }
}
