package kmp;

import java.util.Objects;

public final class KmpSearchRequest {

    private final String pattern;
    private final String text;

    private KmpSearchRequest(String pattern, String text) {
        this.pattern = pattern;
        this.text = text;
    }

    public static KmpSearchRequest of(CharSequence pattern, CharSequence text) {
        if (pattern == null || text == null) {
            throw new IllegalArgumentException("Pattern and text must not be null.");
        }

        return new KmpSearchRequest(pattern.toString(), text.toString());
    }

    public String pattern() {
        return pattern;
    }

    public String text() {
        return text;
    }

    public int patternLength() {
        return pattern.length();
    }

    public int textLength() {
        return text.length();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof KmpSearchRequest)) {
            return false;
        }
        KmpSearchRequest that = (KmpSearchRequest) other;
        return pattern.equals(that.pattern) && text.equals(that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pattern, text);
    }

    @Override
    public String toString() {
        return "KmpSearchRequest{pattern='" + pattern + "', text='" + text + "'}";
    }
}
