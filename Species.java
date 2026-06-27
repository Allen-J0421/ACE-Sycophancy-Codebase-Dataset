/**
 * Identifiers for the different organism types in the simulation.
 *
 * @version 2022.03.02
 */
public enum Species
{
    HIPPOPOTAMUS("Hippopotamus"),
    LEOPARD("Leopard"),
    BEAR("Bear"),
    MONKEY("Monkey"),
    SLOTH("Sloth"),
    PLANT("Plant");

    private final String displayName;

    Species(String displayName)
    {
        this.displayName = displayName;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public String getConfigKey()
    {
        return name().toLowerCase();
    }
}
