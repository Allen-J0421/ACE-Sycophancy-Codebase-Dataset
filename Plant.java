/**
 * Write a description of class Plant here.
 *
 * @version (a version number or a date)
 */

public class Plant extends Organism
{
    private static final OrganismAttributes ATTRIBUTES =
        ConfiguredAttributes.organism(Species.PLANT, Plant::new,
                                      OrganismBehaviors.AGE_BASED_BREEDING,
                                      OrganismBehaviors.STATIONARY,
                                      OrganismBehaviors.INCREMENT_AGE,
                                      OrganismBehaviors.GIVE_BIRTH,
                                      OrganismBehaviors.RELOCATE);
    
    public Plant(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location, ATTRIBUTES);
    }
}
