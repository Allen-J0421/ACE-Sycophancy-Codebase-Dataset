record BracketMatcher(BracketConfig config) {

    boolean isOpener(char c) {
        for (char o : config.openers) if (o == c) return true;
        return false;
    }

    boolean isCloser(char c) {
        for (char cl : config.closers) if (cl == c) return true;
        return false;
    }

    boolean matches(char opener, char closer) {
        for (int i = 0; i < config.closers.length; i++) {
            if (config.closers[i] == closer) return config.openers[i] == opener;
        }
        return false;
    }
}
