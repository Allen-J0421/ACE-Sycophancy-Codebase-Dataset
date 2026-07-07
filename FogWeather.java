import java.util.List;

public class FogWeather extends WeatherEvent {
    public String getName() { return "fog"; }

    public void apply(List<Animal> animals, List<Plant> plants) {
        resetFlags(animals, plants);
        for(int i = 0; i < animals.size(); i++) {
            animals.get(i).setFog();
        }
    }
}
