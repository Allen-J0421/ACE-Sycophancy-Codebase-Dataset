/**
 * A simple model of a Lion.
 * Lions age, move, eat deers/cats/owls/mouse, and die.
 *
 * @version 2022.03.02 
 */
public class Lion extends BreedingAnimal
{
    private static final AnimalProfile PROFILE = new AnimalProfile(225, 275, 40, 20);
    private static final BreedingProfile BREEDING_PROFILE =
        new BreedingProfile(10, 0.10, 5, true, 2);

    /**
     * Create a Lion. A Lion can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the Lion will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Lion(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location, PROFILE, BREEDING_PROFILE);
    }

    @Override
    protected void updateStatus(SimulationStep step)
    {
        decrementFoodLevel();
    }
    
    /**
     * Look for deer, cat, owl ,mouse adjacent to the current location.
     * Only the first live deer/cat/owl/mouse is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood()
    {
        Location preyLocation = eat(Deer.class);
        if(preyLocation != null) {
            return preyLocation;
        }

        preyLocation = eat(Cat.class);
        if(preyLocation != null) {
            return preyLocation;
        }

        preyLocation = eat(Owl.class);
        if(preyLocation != null) {
            return preyLocation;
        }

        return eat(Mouse.class);
    }
    
    @Override
    protected Animal createYoung(Field field, Location location)
    {
        return new Lion(false, field, location);
    }

    @Override
    protected Location selectMoveLocation(SimulationStep step)
    {
        if(step.getWeather() == Weather.FOGGY) {
            return null;
        }
        return findFood();
    }

    @Override
    protected boolean shouldMove(SimulationStep step)
    {
        return !step.isNight();
    }
        
    /**
     * Hunt a specific prey species adjacent to this lion.
     */
    private <T extends Animal> Location eat(Class<T> preyClass)
    {
        return findAdjacentLocation(preyClass, 1, prey -> {
            if(prey.isAlive()) {
                prey.setDead();
                changeFoodLevel(prey.foodValue());
                return true;
            }
            return false;
        });
    }

}
