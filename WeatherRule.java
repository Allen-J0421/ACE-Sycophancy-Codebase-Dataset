/**
 * Weighted rule for selecting a weather type.
 */
public class WeatherRule {

    private final WeatherType type;
    private final double probability;

    public WeatherRule(WeatherType type, double probability) {
        this.type = type;
        this.probability = probability;
    }

    public WeatherType getType() {
        return type;
    }

    public double getProbability() {
        return probability;
    }
}
