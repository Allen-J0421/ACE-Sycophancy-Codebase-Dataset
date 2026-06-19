package lcs;

import java.util.Objects;

record LcsInput(CharSequence first, CharSequence second) {
    LcsInput {
        Objects.requireNonNull(first, "first");
        Objects.requireNonNull(second, "second");
    }

    static LcsInput of(CharSequence first, CharSequence second) {
        return new LcsInput(first, second);
    }

    boolean isEmpty() {
        return first.isEmpty() || second.isEmpty();
    }

    CharSequence longer() {
        return first.length() >= second.length() ? first : second;
    }

    CharSequence shorter() {
        return first.length() < second.length() ? first : second;
    }
}
