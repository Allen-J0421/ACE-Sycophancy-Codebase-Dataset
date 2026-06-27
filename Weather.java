import java.util.List;
import java.util.Random;

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

    /**
     * Constructor for objects of class Weather
     */
    public Weather() {}

    /**
     * Advance the weather by one lifecycle tick.
     * @param context Shared lifecycle state for the current step.
     */
    @Override
    public void tick(SimulationContext context) {
        generateWater = false;
        // Generate one weather event per step using shared randomness.
        Random rand = Randomizer.getRandom();
        if (rand.nextDouble() <= RAIN_PROB) {
            makeRain(context.getActors());
            generateWater = true;
            context.requestWaterGeneration();
        }
        else if (rand.nextDouble() <= FOG_PROB){
            makeFog(context.getActors());
        }
        else if (rand.nextDouble() <= HEATWAVE_PROB) {
            makeHeatWave(context.getActors());
        }
    }

    /**
     * What happens when rain occurs. 
     * Water value for all objects are increased by 5, which is the water value of rain.
     * This in turn affects each object's behaviour.
     * @param newActors A list to receive any species affected by the weather event
     */
    protected boolean makeRain(List<Actor> actorsList){
        adjustMoisture(actorsList, 5, 10);
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
        for (Actor actor : actorsList) {
            if (actor instanceof Plant) {
                Plant plant = (Plant) actor;
                plant.setWaterLevel(plant.getWaterLevel() + 2);
            }
            else if (actor instanceof Animal) {
                Animal animal = (Animal) actor;
                animal.setSleepStatus();
            }
            else if (actor instanceof WaterSources){
                WaterSources source = (WaterSources) actor;
                source.setVolume(source.getVolume() + 2);
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
        for (Actor actor : actorsList) {
            if (actor instanceof Plant) {
                Plant plant = (Plant) actor;
                plant.setWaterLevel(plant.getWaterLevel() / 2);
            }
            else if (actor instanceof Animal) {
                Animal animal = (Animal) actor;
                animal.setWaterLevel(animal.getWaterLevel() / 2);
            }
            else if (actor instanceof WaterSources){
                WaterSources source = (WaterSources) actor;
                source.setVolume(source.getVolume() / 2);
            }
        }
    }

    /**
     * Increase hydration for living things and water volume for water sources.
     * @param actorsList The active actors.
     * @param organismDelta Water added to plants and animals.
     * @param waterDelta Water added to water sources.
     */
    private void adjustMoisture(List<Actor> actorsList, int organismDelta, int waterDelta)
    {
        for (Actor actor : actorsList) {
            if (actor instanceof Plant) {
                Plant plant = (Plant) actor;
                plant.setWaterLevel(plant.getWaterLevel() + organismDelta);
            }
            else if (actor instanceof Animal) {
                Animal animal = (Animal) actor;
                animal.setWaterLevel(animal.getWaterLevel() + organismDelta);
            }
            else if (actor instanceof WaterSources) {
                WaterSources source = (WaterSources) actor;
                source.setVolume(source.getVolume() + waterDelta);
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
