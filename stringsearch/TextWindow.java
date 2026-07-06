package stringsearch;

public final class TextWindow implements CharSequence {
    private final CharSequence text;
    private final int start;
    private final int length;

    public TextWindow(CharSequence text, int start, int length) {
        this.text = text;
        this.start = start;
        this.length = length;
    }

    @Override public char charAt(int index) { return text.charAt(start + index); }
    @Override public int length() { return length; }
    @Override public CharSequence subSequence(int s, int e) { return new TextWindow(text, start + s, e - s); }
    @Override public String toString() { return text.subSequence(start, start + length).toString(); }

    public int start() { return start; }

    /** First character of this window; leaves the window when it slides forward. */
    public char leaving() { return charAt(0); }

    /** First character past this window; enters the window when it slides forward. */
    public char entering() { return text.charAt(start + length); }

    public TextWindow slide() { return new TextWindow(text, start + 1, length); }

    public boolean startsWith(CharSequence pattern) {
        for (int j = 0; j < pattern.length(); j++) {
            if (charAt(j) != pattern.charAt(j)) return false;
        }
        return true;
    }
}
