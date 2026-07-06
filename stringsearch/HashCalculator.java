package stringsearch;

public interface HashCalculator {
    int hash(CharSequence seq, int length);
    int highOrderFactor(int patternLength);
    int roll(int currentHash, char leaving, char entering, int highOrderFactor);
}
