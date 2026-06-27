import java.util.List;

/**
 * Shared action commands and strategies for organism behaviour.
 *
 * @version 2022.03.02
 */
public final class OrganismBehaviors
{
    public static final OrganismActionCommand INCREMENT_AGE =
        new OrganismActionCommand() {
            public void execute(Organism organism, List<Organism> newOrganisms)
            {
                organism.incrementAge();
            }
        };

    public static final OrganismActionCommand GIVE_BIRTH =
        new OrganismActionCommand() {
            public void execute(Organism organism, List<Organism> newOrganisms)
            {
                organism.giveBirth(newOrganisms);
            }
        };

    public static final OrganismActionCommand RELOCATE =
        new OrganismActionCommand() {
            public void execute(Organism organism, List<Organism> newOrganisms)
            {
                organism.relocate();
            }
        };

    public static final OrganismActionCommand DECAY_HEALTH =
        new OrganismActionCommand() {
            public void execute(Organism organism, List<Organism> newOrganisms)
            {
                ((Animal) organism).incrementHealth();
            }
        };

    public static final OrganismActionCommand APPLY_ILLNESS =
        new OrganismActionCommand() {
            public void execute(Organism organism, List<Organism> newOrganisms)
            {
                ((Infectable) organism).illness();
            }
        };

    public static final OrganismActionCommand SPREAD_DISEASE =
        new OrganismActionCommand() {
            public void execute(Organism organism, List<Organism> newOrganisms)
            {
                ((Infectable) organism).spreadDisease();
            }
        };

    public static final BreedingStrategy AGE_BASED_BREEDING =
        new BreedingStrategy() {
            public boolean canBreed(Organism organism)
            {
                return organism.getAge() >= organism.getAttributes().getBreedingAge();
            }
        };

    public static final BreedingStrategy MATE_REQUIRED_BREEDING =
        new BreedingStrategy() {
            public boolean canBreed(Organism organism)
            {
                return ((Animal) organism).hasBreedingMate();
            }
        };

    public static final RelocationStrategy STATIONARY =
        new RelocationStrategy() {
            public Location findNextLocation(Organism organism)
            {
                return null;
            }

            public boolean diesWhenBlocked()
            {
                return false;
            }
        };

    public static final RelocationStrategy FORAGE_OR_WANDER =
        new RelocationStrategy() {
            public Location findNextLocation(Organism organism)
            {
                Animal animal = (Animal) organism;
                Location newLocation = animal.findFood();
                if(newLocation == null) {
                    newLocation = animal.getField().freeAdjacentLocation(animal.getLocation());
                }
                return newLocation;
            }

            public boolean diesWhenBlocked()
            {
                return true;
            }
        };

    private OrganismBehaviors()
    {
    }
}
