/**
 * Weather condition: Rain.
 * Increases water level of plants and animals; increases volume of water sources.
 * Also signals that new lake tiles should be generated.
 *
 * @version 2022.03.01
 */
public class RainCondition extends WeatherCondition
{
    public RainCondition()
    {
        super(SimulationConfiguration.RAIN_DURATION, true);
    }

    @Override
    public void applyEffect(Actor actor)
    {
        if(actor instanceof Plant) {
            Plant plant = (Plant) actor;
            plant.setWaterLevel(plant.getWaterLevel() + SimulationConfiguration.RAIN_WATER_BONUS);
        }
        else if(actor instanceof Animal) {
            Animal animal = (Animal) actor;
            animal.setWaterLevel(animal.getWaterLevel() + SimulationConfiguration.RAIN_WATER_BONUS);
        }
        else if(actor instanceof WaterSources) {
            WaterSources source = (WaterSources) actor;
            source.setVolume(source.getVolume() + SimulationConfiguration.RAIN_VOLUME_BONUS);
        }
    }
}
