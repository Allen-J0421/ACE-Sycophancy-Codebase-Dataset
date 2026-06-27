/**
 * A class representing shared identity and disease-related characteristics of animals.
 *
 * @version 2022/03/02
 */
public abstract class Animal extends Creature
{
    // sex of an animal, 0 = female and 1 = male
    private final int sex;

    // If the animal is infected by disease.
    private boolean isInfected;
    // If the animal is immuned from the diease.
    private boolean isImmuned;

    // Track the first step at which the animal is infected;
    protected int infectionStartStep;

    // total population that is die of disease.
    public static int populationDieOfDisease = 0;

    public Animal(Field field, Location location)
    {
        super(field, location);
        sex = (int)(Math.round(Math.random()));
        isInfected = false;
        isImmuned = false;
        infectionStartStep = 0;
    }

    /**
     * get the gender of an animal.
     * @return sex  0 = female and 1 = male.
     */
    public int getSex()
    {
        return sex;
    }

    /**
     * get whether an animal is infected.
     *
     * @return true if an animal is infected, false otherwise.
     */
    public boolean getIsInfected()
    {
        return isInfected;
    }

    /**
     * get if an animal is immuned.
     *
     * @return true if an animal is immuned, false otherwise.
     */
    public boolean getIsImmuned()
    {
        return isImmuned;
    }

    /**
     * set an animal to be infected.
     * @param isInfected
     */
    public void setIsInfected(boolean isInfected)
    {
        this.isInfected = isInfected;
    }

    /**
     * set an animal to be immuned.
     * @param isImmuned
     */
    public void setIsImmuned(boolean isImmuned)
    {
        this.isImmuned = isImmuned;
    }

    /**
     *  Make an animal infected while the disease exists.
     *
     *  @param disease disease
     *  @param step current step.
     */
    protected void makeInfected(SimulationContext context)
    {
        Disease disease = context.getDisease();
        if((!this.getIsImmuned()) && Randomizer.getRandom().nextDouble() <= disease.INFECTION_RATE) {
            setIsInfected(true);
        }

        // if the animal is infected in current step, record its start step.
        if(getIsInfected() && infectionStartStep == 0) {
            infectionStartStep = context.getStep();
        }
    }

    /**
     * give the animal immunity while condition is met.
     * @param disease disease.
     * @param step int step.
     */
    protected void ifCanGrantImmunity(SimulationContext context)
    {
        Disease disease = context.getDisease();
        // if an animal is infected, it may die. Otherwise assume it gets immuntity from that disease.
        if(getIsInfected() && !getIsImmuned()) {
            if(context.getStep() - infectionStartStep >= disease.NUMBER_OF_STEP_TO_WITHSTAND) {
                setIsImmuned(true);
                setIsInfected(false);
            }
        }
    }

    /**
     * set an animal to death if it is die of infection.
     * Return true if an animal dies of infection
     *
     * @return true if an animal dies of infection.
     */
    protected boolean dieOfInfection(SimulationContext context)
    {
        Disease disease = context.getDisease();
        // if an animal is infected, it may die. Otherwise assume it gets immuntity from that disease.
        if(getIsInfected() && !getIsImmuned()) {
            if(Randomizer.getRandom().nextDouble() <= disease.MORTALITY_RATE) {
                setDead();
                populationDieOfDisease++;
                return true;
            }
        }
        return false;
    }
}
