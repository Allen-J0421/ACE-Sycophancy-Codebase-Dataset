import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Centralizes adjacency, target selection, and movement-related rules.
 */
public class MovementService
{
    private static final Class<? extends Organism> DEFAULT_FALLBACK_SPECIES = Grass.class;

    private final RandomProvider randomProvider;
    private final Map<Class<? extends Organism>, MovementRule> movementRules;

    public MovementService(RandomProvider randomProvider)
    {
        this.randomProvider = randomProvider;
        this.movementRules = Map.of(
                Eagle.class,
                new MovementRule(DEFAULT_FALLBACK_SPECIES, environment -> environment.getWeatherService().allowsEagleForaging())
        );
    }

    public void moveOrganism(Organism organism, Location location)
    {
        organism.setLocation(location);
    }

    public List<Location> getAdjacentLocations(Field field, Location location)
    {
        assert location != null : "Null location passed to getAdjacentLocations";
        List<Location> locations = new LinkedList<>();
        int row = location.getRow();
        int col = location.getCol();
        for(int roffset = -1; roffset <= 1; roffset++) {
            int nextRow = row + roffset;
            if(nextRow >= 0 && nextRow < field.getDepth()) {
                for(int coffset = -1; coffset <= 1; coffset++) {
                    int nextCol = col + coffset;
                    if(nextCol >= 0
                            && nextCol < field.getWidth()
                            && (roffset != 0 || coffset != 0)) {
                        locations.add(new Location(nextRow, nextCol));
                    }
                }
            }
        }

        Collections.shuffle(locations, randomProvider.getRandom());
        return locations;
    }

    public List<Location> getFreeAdjacentLocations(Field field, Location location)
    {
        return getFreeAdjacentLocations(field, getAdjacentLocations(field, location));
    }

    public Location findFreeAdjacentLocation(Field field, Location location)
    {
        return firstOrNull(getFreeAdjacentLocations(field, location));
    }

    public List<Location> findAdjacentLocationsWithSpecies(Field field, Location location, Class<?> speciesClass)
    {
        return findAdjacentLocationsWithSpecies(field, getAdjacentLocations(field, location), speciesClass);
    }

    public List<Organism> findPotentialMates(Animal animal)
    {
        List<Organism> potentialMates = new ArrayList<>();
        Field field = animal.getField();
        if(field != null) {
            for(Location location : getAdjacentLocations(field, animal.getLocation())) {
                Object nearby = field.getObjectAt(location);
                if(nearby instanceof Animal mate
                        && mate.getClass().equals(animal.getClass())
                        && mate.sex != animal.sex) {
                    potentialMates.add(mate);
                }
            }
        }
        return potentialMates;
    }

    public List<Location> getRandomFreePatches(Field field, double rateOfGeneration)
    {
        List<Location> patches = getFreePatches(field);
        List<Location> randomPatches = new ArrayList<>();
        for(int i = 0; i < rateOfGeneration * patches.size() && !patches.isEmpty(); i++) {
            Location selectedPatch = patches.get(randomProvider.getRandom().nextInt(patches.size()));
            randomPatches.add(selectedPatch);
            for(Location location : getAdjacentLocations(field, selectedPatch)) {
                patches.remove(location);
            }
            patches.remove(selectedPatch);
        }
        return randomPatches;
    }

    public MovementDecision resolveAnimalMovement(Animal animal,
            Environment environment,
            List<Location> adjacentLocations)
    {
        MovementRule rule = getMovementRule(animal.getClass());
        if(rule.canForage(environment)) {
            MovementDecision foodTarget = findTarget(fieldOf(animal), adjacentLocations, animal.getDiet());
            if(foodTarget != null) {
                return foodTarget;
            }
        }

        return resolveFallbackMovement(fieldOf(animal), adjacentLocations, rule);
    }

    public MovementDecision resolveHunterMovement(Hunter hunter, List<Location> adjacentLocations)
    {
        MovementDecision preyTarget = findTarget(fieldOf(hunter), adjacentLocations, hunter.getDiet());
        if(preyTarget != null) {
            return preyTarget;
        }

        return resolveFallbackMovement(fieldOf(hunter), adjacentLocations, getMovementRule(Hunter.class));
    }

    private Field fieldOf(Organism organism)
    {
        return organism.getField();
    }

    private MovementDecision resolveFallbackMovement(Field field,
            List<Location> adjacentLocations,
            MovementRule rule)
    {
        Location freeLocation = firstOrNull(getFreeAdjacentLocations(field, adjacentLocations));
        if(freeLocation != null) {
            return new MovementDecision(freeLocation, null, false);
        }

        Location speciesFallback = pickRandomLocation(
                findAdjacentLocationsWithSpecies(field, adjacentLocations, rule.fallbackSpecies())
        );
        if(speciesFallback != null) {
            return new MovementDecision(speciesFallback, null, false);
        }

        return new MovementDecision(null, null, true);
    }

    private MovementDecision findTarget(Field field,
            List<Location> adjacentLocations,
            Set<Class<? extends Organism>> targetSpecies)
    {
        for(Location location : adjacentLocations) {
            Object occupant = field.getObjectAt(location);
            if(occupant instanceof Organism organism && targetSpecies.contains(occupant.getClass())) {
                if(organism.isAlive()) {
                    return new MovementDecision(location, organism, false);
                }
                return new MovementDecision(location, null, false);
            }
        }
        return null;
    }

    private List<Location> getFreeAdjacentLocations(Field field, List<Location> adjacentLocations)
    {
        List<Location> free = new LinkedList<>();
        for(Location location : adjacentLocations) {
            if(field.getObjectAt(location) == null) {
                free.add(location);
            }
        }
        return free;
    }

    private List<Location> findAdjacentLocationsWithSpecies(Field field,
            List<Location> adjacentLocations,
            Class<?> speciesClass)
    {
        List<Location> matches = new ArrayList<>();
        for(Location location : adjacentLocations) {
            Object occupant = field.getObjectAt(location);
            if(occupant != null && occupant.getClass() == speciesClass) {
                matches.add(location);
            }
        }
        return matches;
    }

    private List<Location> getFreePatches(Field field)
    {
        List<Location> freePatches = new ArrayList<>();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                Location center = new Location(row, col);
                if(field.getObjectAt(center) == null && isPatchFree(field, center)) {
                    freePatches.add(center);
                }
            }
        }
        return freePatches;
    }

    private boolean isPatchFree(Field field, Location center)
    {
        for(Location location : getAdjacentLocations(field, center)) {
            if(field.getObjectAt(location) != null) {
                return false;
            }
        }
        return true;
    }

    private Location pickRandomLocation(List<Location> locations)
    {
        if(locations.isEmpty()) {
            return null;
        }
        return locations.get(randomProvider.getRandom().nextInt(locations.size()));
    }

    private Location firstOrNull(List<Location> locations)
    {
        if(locations.isEmpty()) {
            return null;
        }
        return locations.get(0);
    }

    public record MovementDecision(Location targetLocation, Organism consumedOrganism, boolean overcrowded) { }

    private record MovementRule(Class<? extends Organism> fallbackSpecies, ForageRule forageRule)
    {
        private boolean canForage(Environment environment)
        {
            return forageRule.canForage(environment);
        }
    }

    @FunctionalInterface
    private interface ForageRule
    {
        boolean canForage(Environment environment);
    }

    private MovementRule getMovementRule(Class<? extends Organism> organismClass)
    {
        return movementRules.getOrDefault(organismClass, new MovementRule(DEFAULT_FALLBACK_SPECIES, environment -> true));
    }
}
