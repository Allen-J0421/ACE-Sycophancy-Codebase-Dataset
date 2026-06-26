
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * A simple model of a Hunter.
 * Hunters age, move, hunt lions and cheetahs at night, and die.
 * A new hunter spawns nearby every SPAWN_INTERVAL steps.
 *
 * @version 2022.03.01
 */
public class Hunter extends Actor

{
    // Characteristics shared by all Hunters.
    private static final int MAX_AGE = 1000;
    private static final int SHOTS = 2;
    // A new hunter spawns every this many simulation steps.
    private static final int SPAWN_INTERVAL = 898;
    private static final Set<Class<? extends Actor>> HUNTABLE_SPECIES = new HashSet<>(Arrays.asList(Lion.class, Cheetah.class));
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
        if(!simulator.isDay()){
            if(isActive() ) {
                hunt(getHuntProbability(simulator.getWeather()));
                Field field = getField();
                Location newLocation = field.freeAdjacentLocation(getLocation());
                if(newLocation != null) {
                    setLocation(newLocation);
                } 
                if(simulator.getSteps() % SPAWN_INTERVAL == 0) {
                    List<Location> free = field.getFreeAdjacentLocations(getLocation());
                    if(!free.isEmpty()) {
                        Location loc = free.remove(0);
                        Hunter hunter = new Hunter(field, loc);
                        newHunters.add(hunter);
                    }
                }
            }
        }
    }

    private double getHuntProbability(Weather weather) {
        switch(weather) {
            case RAINY: return 0.7;
            case FOGGY: return 0.4;
            default:    return getRandom().nextDouble();
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
            for(int i = 0; i < SHOTS && it.hasNext(); i++){
                Location where = it.next();
                Object animal = field.getObjectAt(where);
                if(animal != null && HUNTABLE_SPECIES.contains(animal.getClass())){
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
     * Gets the max age of a hunter.
     * @return int. Max age of the hunter.
     */
    protected int getMaxAge(){
        return MAX_AGE;
    }
}
