import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Stores a bounded history of recent weather events.
 */
public class WeatherHistory {

    private final int maxEntries;
    private final List<WeatherType> recentWeather;

    public WeatherHistory(int maxEntries) {
        this.maxEntries = maxEntries;
        this.recentWeather = new ArrayList<>();
    }

    public void record(WeatherType weatherType) {
        recentWeather.add(weatherType);
        if (recentWeather.size() > maxEntries) {
            recentWeather.remove(0);
        }
    }

    public List<WeatherType> asList() {
        return Collections.unmodifiableList(recentWeather);
    }
}
