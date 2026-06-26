import java.util.List;

/**
 * A simple model of a whale.
 *
 * Whales age, move, eat cod or salmon, consume oxygen, propagate,
 * and may get infected by disease and die of that or of weather.
 *
 * @version 2022/03/02
 */
public class Whale extends Animal
{
    private static final int BREEDING_AGE = 6;
    private static final int MAX_AGE = 150;
    private static final double BREEDING_PROBABILITY = 0.2;
    private static final int MAX_LITTER_SIZE = 8;
    private static final int COD_FOOD_VALUE = 8;
    private static final int SALMON_FOOD_VALUE = 8;

    public Whale(boolean randomAge, Field field, Location location) {
        super(field, location);
        if(randomAge) {
            age = Randomizer.getRandom().nextInt(MAX_AGE);
            foodLevel = Randomizer.getRandom().nextInt(COD_FOOD_VALUE);
        } else {
            age = 0;
            foodLevel = COD_FOOD_VALUE;
        }
    }

    protected int getBreedingAge()            { return BREEDING_AGE; }
    protected int getMaxAge()                 { return MAX_AGE; }
    protected double getBreedingProbability() { return BREEDING_PROBABILITY; }
    protected int getMaxLitterSize()          { return MAX_LITTER_SIZE; }

    protected Animal createYoung(Field field, Location location) {
        return new Whale(false, field, location);
    }

    /**
     * Hunt Cod or Salmon adjacent to the current location.
     * Spreads disease from any infected neighbour encountered; kills the first live prey found.
     */
    public Location search(Disease disease, int step) {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation(), 1);
        for(Location loc : adjacent) {
            Object creature = field.getObjectAt(loc);
            if(creature instanceof Animal && ((Animal) creature).getIsInfected()) {
                makeInfected(disease, step);
            }
            if(creature instanceof Cod || creature instanceof Salmon) {
                Animal prey = (Animal) creature;
                if(prey.isAlive()) {
                    prey.setDead();
                    foodLevel = (prey instanceof Cod) ? COD_FOOD_VALUE : SALMON_FOOD_VALUE;
                    return loc;
                }
            }
        }
        return null;
    }
}
