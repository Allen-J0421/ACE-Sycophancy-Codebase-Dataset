/**
 * Weather condition: Heatwave.
 * Halves the water level of all organisms and the volume of all water sources.
 *
 * @version 2022.03.01
 */
public class HeatwaveCondition extends WeatherCondition
{
    public HeatwaveCondition()
    {
        super(SimulationConfiguration.HEATWAVE_DURATION, false);
    }

    @Override
    public void applyEffect(Actor actor)
    {
        if(actor instanceof Plant) {
            Plant plant = (Plant) actor;
            plant.setWaterLevel(plant.getWaterLevel() / SimulationConfiguration.HEATWAVE_WATER_DIVISOR);
        }
        else if(actor instanceof Animal) {
            Animal animal = (Animal) actor;
            animal.setWaterLevel(animal.getWaterLevel() / SimulationConfiguration.HEATWAVE_WATER_DIVISOR);
        }
        else if(actor instanceof WaterSources) {
            WaterSources source = (WaterSources) actor;
            source.setVolume(source.getVolume() / SimulationConfiguration.HEATWAVE_WATER_DIVISOR);
        }
    }
}
