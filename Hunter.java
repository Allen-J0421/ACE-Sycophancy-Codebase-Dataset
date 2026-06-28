
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
        if(!simulator.isDay()){
            if(isActive() ) {
                switch(simulator.getWeather()){
                    case RAINY:
                        hunt(0.7);
                        break;
                    case FOGGY:
                        hunt(0.4);
                        break;
                    default:
                        hunt(getRandom().nextDouble());
                        break;
                }
                Field field = getField();
                Location newLocation = field.freeAdjacentLocation(getLocation());
                if(newLocation != null) {
                    setLocation(newLocation);
                } 
                //hunter number increases as there are more hunters in the area who want to hunt.
                if (simulator.getSteps() % 898 ==  0){
                    List<Location> free = field.getFreeAdjacentLocations(getLocation());
                    if(free.size() > 0) {
                        Location loc = free.remove(0);
                        Hunter hunter = new Hunter(field, loc);
                        newHunters.add(hunter);
                    }
                }
            }
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
     * Gets the max age of a hunter.
     * @return int. Max age of the hunter.
     */
    protected int getMaxAge(){
        return MAX_AGE;
    }
}
