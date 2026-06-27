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
    // Probability of rain occurring
    private static final double RAIN_PROB = 0.12;
    // Probability of fog occurring
    private static final double FOG_PROB = 0.005;
    // Probability of a heatwave occurring 
    private static final double HEATWAVE_PROB = 0.0001;
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
        //generate a random value and compare it with the probability of
        //different types of weather occurring.
        Random rand = Randomizer.getRandom();
        if (rand.nextDouble() <= RAIN_PROB) {
            generateWater = makeRain(actorsList);
        }
        else if (rand.nextDouble() <= FOG_PROB){
            makeFog(actorsList);
        }
        else if (rand.nextDouble() <= HEATWAVE_PROB) {
            makeHeatWave(actorsList);
        }
    }

    /**
     * What happens when rain occurs. 
     * Water value for all objects are increased by 5, which is the water value of rain.
     * This in turn affects each object's behaviour.
     * @param newActors A list to receive any species affected by the weather event
     */
    protected boolean makeRain(List<Actor> actorsList){
        //it will now rain for 3 steps
        int current = simulator.getStep();
        int stop = current + 3;
        ArrayList<WaterSources> newWater = new ArrayList<>();
        while (current <= stop) {
            //iterate over all actors in the simulation
            Iterator<Actor> it = actorsList.iterator();
            while(it.hasNext()) {
                Actor actor = it.next();
                //add rain water value to each actor's water level
                if (actor instanceof Plant) {
                    Plant plant = (Plant) actor;
                    int newWaterLevel = plant.getWaterLevel() + 5;
                    plant.setWaterLevel(newWaterLevel);
                }
                else if (actor instanceof Animal) {
                    Animal animal = (Animal) actor;
                    int newWaterLevel = animal.getWaterLevel() + 5;
                    animal.setWaterLevel(newWaterLevel);
                }
                else if (actor instanceof WaterSources){
                    WaterSources source = (WaterSources) actor;
                    int newVolume = source.getVolume() + 10;
                    source.setVolume(newVolume);
                }
                current++;
            }
        }
        return true;
    }

    /**
     * What happens when fog occurs. 
     * Water value for some objects are increased by 2 
     * and causes animals to not be able to conduct normal behaviour when there is gof.
     * This in turn affects each object's behaviour.
     * @param newActors A list to receive any species affected by the weather event
     */
    protected void makeFog(List<Actor> actorsList) {
        //fog occurs for 1 step
        int current = simulator.getStep();
        int stop = current + 1;
        while (current <= stop) {
            //iterate over all actors in the simulation
            Iterator<Actor> it = actorsList.iterator();
            while(it.hasNext()) {
                Actor actor = it.next();
                //plant's & water sources' water levels increase a bit
                if (actor instanceof Plant) {
                    Plant plant = (Plant) actor;
                    int newWaterLevel = plant.getWaterLevel() + 2;
                    plant.setWaterLevel(newWaterLevel);
                }
                else if (actor instanceof Animal) {
                    //animals can't do anything
                    Animal animal = (Animal) actor;
                    animal.setSleepStatus();
                }
                else if (actor instanceof WaterSources){
                    WaterSources source = (WaterSources) actor;
                    int newVolume = source.getVolume() + 2;
                    source.setVolume(newVolume);
                }
                current++;
            }
        }
    }

    /**
     * What happens when a heatwave occurs. 
     * Water value for all objects is halved by 2 to show impact of extreme exposure to heat.
     * This in turn affects each object's behaviour.
     * @param newActors A list to receive any species affected by the weather event
     */
    protected void makeHeatWave(List<Actor> actorsList){
        //heatwave occurs for one step
        int current = simulator.getStep();
        int stop = current + 1;
        while (current <= stop) {
            //iterate over all actors in the simulation
            Iterator<Actor> it = actorsList.iterator();
            while(it.hasNext()) {
                Actor actor = it.next();
                //water value halved by 2 for all actors
                if (actor instanceof Plant) {
                    Plant plant = (Plant) actor;
                    int newWaterLevel = plant.getWaterLevel() / 2;
                    plant.setWaterLevel(newWaterLevel);
                }
                else if (actor instanceof Animal) {
                    Animal animal = (Animal) actor;
                    int newWaterLevel = animal.getWaterLevel() / 2;
                    animal.setWaterLevel(newWaterLevel);
                }
                else if (actor instanceof WaterSources){
                    WaterSources source = (WaterSources) actor;
                    int newVolume = source.getVolume() / 2;
                    source.setVolume(newVolume);
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
