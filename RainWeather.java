import java.util.List;

public class RainWeather extends WeatherEvent {
    public String getName() { return "rain"; }

    public void apply(List<Animal> animals, List<Plant> plants) {
        resetFlags(animals, plants);
        for(int i = 0; i < plants.size(); i++) {
            plants.get(i).setRain();
        }
    }
}
