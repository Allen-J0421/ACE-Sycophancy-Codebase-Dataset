import java.util.stream.IntStream;

class BracketValidator {
    private final BracketConfig config;

    BracketValidator(BracketConfig config) {
        this.config = config;
    }

    void validate(String s) {
        ValidationContext ctx = new ValidationContext(new BracketMatcher(config));
        IntStream.range(0, s.length())
            .forEach(i -> ctx.process(s.charAt(i), i));
        ctx.checkComplete();
    }
}
