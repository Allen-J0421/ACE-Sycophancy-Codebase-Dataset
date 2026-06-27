/**
 * Factory for foraging animal behavior profiles.
 */
public final class ForagingBehaviorFactory
{
    private ForagingBehaviorFactory()
    {
    }

    public static ForagingBehavior forAnimal(Class<? extends ForagingAnimal> animalClass)
    {
        if(animalClass == Cod.class) {
            return new ForagingBehavior(6, 50, 0.3, 10, 13, 1, Cod.class, 1, true,
                (field, location) -> new Cod(false, field, location), Seaweed.class);
        }
        if(animalClass == Salmon.class) {
            return new ForagingBehavior(4, 50, 0.3, 15, 13, 1, Salmon.class, 2, true,
                (field, location) -> new Salmon(false, field, location), Seaweed.class);
        }
        if(animalClass == Shark.class) {
            return new ForagingBehavior(6, 150, 0.4, 8, 8, 1, Cod.class, 2, false,
                (field, location) -> new Shark(false, field, location), Cod.class, Salmon.class);
        }
        if(animalClass == Whale.class) {
            return new ForagingBehavior(6, 150, 0.2, 8, 8, 1, Salmon.class, 2, false,
                (field, location) -> new Whale(false, field, location), Cod.class, Salmon.class);
        }

        throw new IllegalArgumentException("Unsupported foraging animal type: " + animalClass.getName());
    }
}
