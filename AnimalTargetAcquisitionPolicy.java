/**
 * Animal-specific target acquisition logic.
 */
public final class AnimalTargetAcquisitionPolicy extends AbstractTargetAcquisitionPolicy
{
    private static final FeedingPolicy FEEDING_POLICY = new StandardFeedingPolicy();

    @Override
    public Location acquireTarget(Organism forager, Environment environment)
    {
        Animal animal = (Animal) forager;
        contractDiseaseFromAdjacentOrganisms(animal, occupant ->
                occupant != null && !(occupant instanceof Hunter));

        Location foodLocation = findAdjacentTarget(animal,
                occupant -> occupant instanceof Edible && animal.DIET().contains(occupant.getClass()));
        if(foodLocation == null) {
            return null;
        }

        FEEDING_POLICY.feed(animal, (Organism) animal.getField().getObjectAt(foodLocation));
        return foodLocation;
    }
}
