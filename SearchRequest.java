import java.util.Objects;

public record SearchRequest(String text, String pattern) {

    public SearchRequest {
        Objects.requireNonNull(text, "text must not be null");
        Objects.requireNonNull(pattern, "pattern must not be null");
    }

    public int textLength() {
        return text.length();
    }

    public int patternLength() {
        return pattern.length();
    }

    public boolean patternIsLongerThanText() {
        return patternLength() > textLength();
    }
}
