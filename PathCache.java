import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

class PathCache {
    private final Map<Integer, Optional<Path>> cache = new LinkedHashMap<>();

    void put(int destination, Optional<Path> path) {
        cache.put(destination, path);
    }

    Optional<Optional<Path>> get(int destination) {
        return Optional.ofNullable(cache.get(destination));
    }

    void clear() {
        cache.clear();
    }

    int size() {
        return cache.size();
    }
}
