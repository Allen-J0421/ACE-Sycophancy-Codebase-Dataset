package stringsearch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public final class MatchResult implements Iterable<Integer> {
    private final List<Integer> positions;

    MatchResult() {
        this.positions = new ArrayList<>();
    }

    MatchResult(int initialCapacity) {
        this.positions = new ArrayList<>(initialCapacity);
    }

    void add(int position) {
        positions.add(position);
    }

    public List<Integer> positions() {
        return Collections.unmodifiableList(positions);
    }

    public int count() {
        return positions.size();
    }

    public boolean hasMatches() {
        return !positions.isEmpty();
    }

    @Override
    public Iterator<Integer> iterator() {
        return positions().iterator();
    }
}
