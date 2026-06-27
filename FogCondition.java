/**
 * Weather condition: Fog.
 * Increases water level of plants; puts animals to sleep; increases volume of water sources.
 *
 * @version 2022.03.01
 */
public class FogCondition extends WeatherCondition
{
    public FogCondition()
    {
        super(SimulationConfiguration.FOG_DURATION, false);
    }

    @Override
    public void applyEffect(Actor actor)
    {
        if(actor instanceof Plant) {
            Plant plant = (Plant) actor;
            plant.setWaterLevel(plant.getWaterLevel() + SimulationConfiguration.FOG_WATER_BONUS);
        }
        else if(actor instanceof Animal) {
            Animal animal = (Animal) actor;
            animal.setSleepStatus();
        }
        else if(actor instanceof WaterSources) {
            WaterSources source = (WaterSources) actor;
            source.setVolume(source.getVolume() + SimulationConfiguration.FOG_VOLUME_BONUS);
        }
    }
}
