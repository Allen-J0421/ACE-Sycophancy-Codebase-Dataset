import java.util.List;

/**
 * A simple model of a cod.
 *
 * Cods age, move, eat seaweed, consume oxygen, propagate,
 * and may get infected by disease and die of that or of weather.
 *
 * @version 2022/03/02
 */
public class Cod extends Animal
{
    private static final int BREEDING_AGE = 6;
    private static final int MAX_AGE = 50;
    private static final double BREEDING_PROBABILITY = 0.3;
    private static final int MAX_LITTER_SIZE = 10;
    private static final int SEAWEED_FOOD_VALUE = 13;

    public Cod(boolean randomAge, Field field, Location location) {
        super(field, location);
        if(randomAge) {
            age = Randomizer.getRandom().nextInt(MAX_AGE);
            foodLevel = Randomizer.getRandom().nextInt(SEAWEED_FOOD_VALUE);
        } else {
            age = 0;
            foodLevel = SEAWEED_FOOD_VALUE;
        }
    }

    protected int getBreedingAge()            { return BREEDING_AGE; }
    protected int getMaxAge()                 { return MAX_AGE; }
    protected double getBreedingProbability() { return BREEDING_PROBABILITY; }
    protected int getMaxLitterSize()          { return MAX_LITTER_SIZE; }

    protected Animal createYoung(Field field, Location location) {
        return new Cod(false, field, location);
    }

    /**
     * Look for seaweed adjacent to the current location.
     * Spreads disease from any infected neighbour encountered; eats the first live seaweed found.
     */
    public Location search(Disease disease, int step) {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation(), 1);
        for(Location loc : adjacent) {
            Object creature = field.getObjectAt(loc);
            if(creature instanceof Animal && ((Animal) creature).isInfected()) {
                makeInfected(disease, step);
            }
            if(creature instanceof Seaweed) {
                Seaweed seaweed = (Seaweed) creature;
                if(seaweed.isAlive()) {
                    seaweed.setDead();
                    foodLevel = SEAWEED_FOOD_VALUE;
                    return loc;
                }
            }
        }
        return null;
    }
}
