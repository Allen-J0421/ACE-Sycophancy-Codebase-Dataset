import java.util.List;
import java.util.Random;

/**
 * Selects the next weather type from weighted rules.
 */
public class WeatherSelector {

    private final List<WeatherRule> rules;
    private final Random random;

    public WeatherSelector(List<WeatherRule> rules, Random random) {
        this.rules = rules;
        this.random = random;
    }

    public WeatherType selectNext(WeatherType fallback) {
        double roll = random.nextDouble();
        double cumulativeProbability = 0;
        for (WeatherRule rule : rules) {
            cumulativeProbability += rule.getProbability();
            if (roll <= cumulativeProbability) {
                return rule.getType();
            }
        }
        return fallback;
    }
}
