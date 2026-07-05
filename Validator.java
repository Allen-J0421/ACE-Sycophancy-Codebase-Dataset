import java.util.Collection;

final class Validator {
    private Validator() {}

    static <T extends Collection<?>> T requireNonEmpty(T collection, String name) {
        if (collection.isEmpty()) throw new IllegalArgumentException(name + " must not be empty");
        return collection;
    }
}
