import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Manages the active organisms in the simulation.
 */
public class OrganismPopulation {

    private final Populator populator;
    private final List<Organism> organisms;

    public OrganismPopulation(Populator populator) {
        this.populator = populator;
        this.organisms = new ArrayList<>();
    }

    public void populate(Field field) {
        organisms.clear();
        populator.populate(organisms, field);
    }

    public void advanceAll(SimulationContext context) {
        List<Organism> newOrganisms = new ArrayList<>();
        for (Iterator<Organism> iterator = organisms.iterator(); iterator.hasNext(); ) {
            Organism organism = iterator.next();
            organism.act(newOrganisms, context);
            if (organism.isRemoved()) {
                iterator.remove();
            }
        }
        organisms.addAll(newOrganisms);
    }
}
