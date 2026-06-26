
import java.util.Iterator;
import java.util.List;

/**
 * A simple model of a Hunter.
 * Hunters age, move, hunt lions, and die.
 * Hunters can bring other hunters after 898 steps to increse the hunter population.
 *
 * @version 2022.03.01
 */
public class Hunter extends Actor

{
    // Characteristics shared by all Hunters.
    private static final int MAX_AGE = 1000;
    private static final int SHOTS = 2;
    private static final int REINFORCEMENT_INTERVAL = 898;
    private static final double RAINY_HUNTING_PROBABILITY = 0.7;
    private static final double FOGGY_HUNTING_PROBABILITY = 0.4;
    /**
     * Create a Hunter. A hunter is created as an adult (age 50).
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Hunter(Field field, Location location)
    {
        super(field, location);
        setAge(50);
        setGrowthLevel(0);
    }

    /**
     * This is what the Hunter does most of the time: it hunts
     * lions during the night. In the process, it might die of old age.
     * 
     * @param newHunters A list to return new Hunters.
     * @param simulator The simulator.
     */
    public void act(List<Actor> newHunters,Simulator simulator){
        incrementAge(simulator.getSteps());
        if(!simulator.isDay() && isActive()) {
            hunt(getHuntingProbability(simulator.getWeather()));
            move();
            addReinforcement(newHunters, simulator.getSteps());
        }
    }

    /**
     * Returns the probability that the hunter successfully hunts in the current weather.
     * @param weather The current weather.
     * @return The hunting success probability.
     */
    private double getHuntingProbability(Weather weather)
    {
        switch(weather){
            case RAINY:
                return RAINY_HUNTING_PROBABILITY;
            case FOGGY:
                return FOGGY_HUNTING_PROBABILITY;
            default:
                return getRandom().nextDouble();
        }
    }

    /**
     * This determine how the hunter hunts nearby lions
     * The hunter has 2 shots.
     * @param probability The probability the hunting is successful.
     */
    private void hunt(double probability){
        if(getRandom().nextDouble() < probability){
            Field field = getField();
            List<Location> adjacent = field.adjacentLocations(getLocation());
            Iterator<Location> it = adjacent.iterator();
            for(int i = 0; i< SHOTS; i++){
                Location where = it.next();
                Object animal = field.getObjectAt(where);
                if( animal != null && (animal instanceof Lion  || animal instanceof Cheetah)){
                    Animal currentAnimal = (Animal) animal;
                    if(currentAnimal.isActive()) { 
                        currentAnimal.setDead();
                        break;
                    }
                }
            }
        }
    }

    /**
     * Move the hunter to a free adjacent location if one is available.
     */
    private void move()
    {
        Field field = getField();
        Location newLocation = field.freeAdjacentLocation(getLocation());
        if(newLocation != null) {
            setLocation(newLocation);
        }
    }

    /**
     * Add a new hunter after the reinforcement interval if space is available.
     * @param newHunters A list to receive newly born hunters.
     * @param step The current simulation step.
     */
    private void addReinforcement(List<Actor> newHunters, int step)
    {
        if(step % REINFORCEMENT_INTERVAL != 0){
            return;
        }

        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        if(free.size() > 0) {
            Location loc = free.remove(0);
            Hunter hunter = new Hunter(field, loc);
            newHunters.add(hunter);
        }
    }

    /**
     * Gets the max age of a hunter.
     * @return int. Max age of the hunter.
     */
    protected int getMaxAge(){
        return MAX_AGE;
    }
}
