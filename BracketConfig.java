import java.util.Map;

final class BracketConfig {
    final char[] closers;
    final char[] openers;

    BracketConfig(char[] closers, char[] openers) {
        this.closers = closers.clone();
        this.openers = openers.clone();
    }

    static BracketConfig from(Map<Character, Character> map) {
        char[] closers = new char[map.size()];
        char[] openers = new char[map.size()];
        int i = 0;
        for (Map.Entry<Character, Character> entry : map.entrySet()) {
            closers[i] = entry.getKey();
            openers[i] = entry.getValue();
            i++;
        }
        return new BracketConfig(closers, openers);
    }
}
