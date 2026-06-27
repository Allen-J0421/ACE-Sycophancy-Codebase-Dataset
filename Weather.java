import java.util.Random;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

/**
 * Models behaviour of simple weather conditions in the simulation.
 * It can rain, be very hot or foggy, which in return affects how species behave.
 *
 * @version 2022.02.27
 */
public class Weather implements Actor
{
    // Indicates whether new water sources need to be generated
    private boolean generateWater = false;
    // The simulator
    private Simulator simulator;

    /**
     * Constructor for objects of class Weather
     */
    public Weather(Simulator simulator)
    {
        this.simulator = simulator;
    }

    /**
     * Make the weather act - that is: make it do
     * whatever it wants/needs to do.
     * @param newActors A list to receive any species affected by the weather event
     */
    public void act(List<Actor> actorsList) {
        Random rand = new Random();
        if(rand.nextDouble() <= SimulationConfiguration.RAIN_PROBABILITY) {
            generateWater = makeRain(actorsList);
        }
        else if(rand.nextDouble() <= SimulationConfiguration.FOG_PROBABILITY) {
            makeFog(actorsList);
        }
        else if(rand.nextDouble() <= SimulationConfiguration.HEATWAVE_PROBABILITY) {
            makeHeatWave(actorsList);
        }
    }

    /**
     * What happens when rain occurs.
     * Water value for all objects are increased, which is the water value of rain.
     * @param actorsList A list to receive any species affected by the weather event
     */
    protected boolean makeRain(List<Actor> actorsList) {
        int current = simulator.getStep();
        int stop = current + SimulationConfiguration.RAIN_DURATION;
        while(current <= stop) {
            Iterator<Actor> it = actorsList.iterator();
            while(it.hasNext()) {
                Actor actor = it.next();
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
                current++;
            }
        }
        return true;
    }

    /**
     * What happens when fog occurs.
     * Water value for some objects are increased and animals cannot act normally.
     * @param actorsList A list to receive any species affected by the weather event
     */
    protected void makeFog(List<Actor> actorsList) {
        int current = simulator.getStep();
        int stop = current + SimulationConfiguration.FOG_DURATION;
        while(current <= stop) {
            Iterator<Actor> it = actorsList.iterator();
            while(it.hasNext()) {
                Actor actor = it.next();
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
                current++;
            }
        }
    }

    /**
     * What happens when a heatwave occurs.
     * Water value for all objects is divided to show impact of extreme heat.
     * @param actorsList A list to receive any species affected by the weather event
     */
    protected void makeHeatWave(List<Actor> actorsList) {
        int current = simulator.getStep();
        int stop = current + SimulationConfiguration.HEATWAVE_DURATION;
        while(current <= stop) {
            Iterator<Actor> it = actorsList.iterator();
            while(it.hasNext()) {
                Actor actor = it.next();
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
                current++;
            }
        }
    }

    /**
     * @return Whether or not the simulator should generate new water space
     * (if it has just rained)
     */
    public boolean generateNewWater() {
        return generateWater;
    }
}
