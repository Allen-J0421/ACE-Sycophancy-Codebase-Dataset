import java.util.List;

/**
 * Animal-specific target acquisition logic.
 */
public final class AnimalTargetAcquisitionPolicy implements TargetAcquisitionPolicy
{
    @Override
    public Location acquireTarget(MobileForager forager)
    {
        Animal animal = (Animal) forager;
        contractDiseaseFromAdjacentOrganisms(animal);

        Location foodLocation = AdjacentTargetSearch.findMatchingLocation(
                animal.getField(),
                animal.getLocation(),
                occupant -> occupant instanceof Edible && animal.DIET().contains(occupant.getClass())
        );
        if(foodLocation == null) {
            return null;
        }

        Organism food = (Organism) animal.getField().getObjectAt(foodLocation);
        if (food.isDiseased()
                && food.getDisease().getDiseaseType() == DiseaseType.FOODBORNE
                && food.getDisease().getPropagationRate() <= Randomizer.getRandom().nextDouble())
        {
            animal.setDisease(food.getDisease());
        }
        if(food.isAlive())
        {
            food.setDead();
            int newFoodLevel = animal.foodLevel + ((Edible) food).getFoodValue();
            animal.foodLevel = Math.min(newFoodLevel, animal.MAX_FOOD_LEVEL());
        }
        return foodLocation;
    }

    private void contractDiseaseFromAdjacentOrganisms(Animal animal)
    {
        Field field = animal.getField();
        List<Location> adjacent = field.adjacentLocations(animal.getLocation());
        for(Location loc : adjacent){
            if(field.getObjectAt(loc) != null && !(field.getObjectAt(loc) instanceof Hunter)){
                Organism organism = (Organism) field.getObjectAt(loc);
                if (organism.isDiseased()
                        && organism.getDisease().getDiseaseType() != DiseaseType.CONTACT
                        && organism.getDisease().getPropagationRate() <= Randomizer.getRandom().nextDouble()){
                    animal.setDisease(organism.getDisease());
                    break;
                }
            }
        }
    }
}
