/**
 * Write a description of class Plant here.
 *
 * @version (a version number or a date)
 */

public class Plant extends Organism
{
    private static final OrganismAttributes ATTRIBUTES =
        new OrganismAttributes(Species.PLANT, true, 5, 15, 0.09, 6, Plant::new);
    
    public Plant(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location, ATTRIBUTES);
    }
}
