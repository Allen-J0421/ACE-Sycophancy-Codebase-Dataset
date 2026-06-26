import java.util.Random;

/**
 * Shared base class for animals that move by choosing between spreading
 * disease and seeking food.
 */
public abstract class ForagingAnimal extends Animal {

    private static final Random rand = Randomizer.getRandom();

    public ForagingAnimal(AnimalAttributes attributes, boolean randomAge, Field field,
                          Location location, OrganismFactory offspringFactory) {
        super(attributes, randomAge, field, location, offspringFactory);
    }

    protected final boolean diesFromDiseaseThisTurn() {
        return rand.nextDouble() <= getDeathByDiseaseProbability();
    }

    protected final Location chooseTargetLocation() {
        if (rand.nextDouble() <= getDiseaseSpreadProbability()) {
            return findAnimalToInfect();
        }
        return findFood();
    }

    protected final void moveToTargetOrRemove(Location targetLocation) {
        Location destination = resolveDestination(targetLocation);
        if (destination != null) {
            setLocation(destination);
        }
        else {
            remove();
        }
    }

    protected Location resolveDestination(Location targetLocation) {
        if (shouldUseFallbackLocation(targetLocation)) {
            return getFallbackLocation();
        }
        return targetLocation;
    }

    protected boolean shouldUseFallbackLocation(Location targetLocation) {
        return targetLocation == null;
    }

    protected Location getFallbackLocation() {
        return getField().freeAdjacentLocation(getLocation());
    }
}
