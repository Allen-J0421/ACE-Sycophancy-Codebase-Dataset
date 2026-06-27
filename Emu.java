import java.awt.Color;

/**
 * A simple model of an emu.
 * Emu age, move, eat grass, and die.
 *
 * @version 01.03.22
 */
public class Emu extends Animal
{
    // Characteristics shared by all emus (class variables).

    // The probability that an emu will be created in any given grid position.
    private static final double CREATION_PROBABILITY = 0.08;
    // The age at which an emu can start to breed.
    private static final int BREEDING_AGE = 30;
    // The age to which an emu can live.
    private static final int MAX_AGE = 600;
    // The likelihood of an emu breeding.
    private static final double BREEDING_PROBABILITY = 0.17;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 7;
    // The food value of a single grass. In effect, this is the
    // number of steps an emu can go before it has to eat again.
    private static final int GRASS_FOOD_VALUE = 60;

    /**
     * Create an emu. An emu can be created as a newborn (age zero
     * and not hungry) or with a random age and food level.
     *
     * @param randomAge If true, the emu will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Emu(boolean randomAge, Field field, Location location) {
        super(randomAge, field, location, MAX_AGE, GRASS_FOOD_VALUE);
    }

    protected int getMaxAge() { return MAX_AGE; }
    protected int getBreedingAge() { return BREEDING_AGE; }
    protected double getBreedingProbability() { return BREEDING_PROBABILITY; }
    protected int getMaxLitterSize() { return MAX_LITTER_SIZE; }

    protected boolean isActiveAt(int time) { return (time <= 9) || (time >= 21); }

    protected Animal createOffspring(Field field, Location location) {
        return new Emu(false, field, location);
    }

    // Eater: emus eat grass and trample other plants (e.g. acacia).
    public int eatGrass(Grass grass) { return GRASS_FOOD_VALUE; }
    public boolean tramplesPlants()  { return true; }

    // Emus cannot be eaten; acceptInteraction() inherits the default -1 from Animal.

    public static final EntityProvider PROVIDER = new EntityProvider() {
        public Class<?> getEntityClass()       { return Emu.class; }
        public Color getColor()                { return Color.YELLOW; }
        public double getCreationProbability() { return CREATION_PROBABILITY; }
        public LivingEntity create(Field f, Location l) { return new Emu(true, f, l); }
    };
}
