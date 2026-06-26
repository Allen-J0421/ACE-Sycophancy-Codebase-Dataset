import java.util.List;

/**
 * Animal-specific target acquisition logic.
 */
public final class AnimalTargetAcquisitionPolicy extends AbstractTargetAcquisitionPolicy
{
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

        consumePrey(animal, (Organism) animal.getField().getObjectAt(foodLocation));
        return foodLocation;
    }
}
