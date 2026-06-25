import java.util.List;
import java.lang.Math;

/**
 * A class representing shared characteristics of animals.
 *
 * @version 2022/03/02
 */
public abstract class Animal extends Creature
{   

    // sex of an animal, 0 = female and 1 = male
    private int sex;

    // The amount of oxygen an animal need to survive
    protected static final double ANIMAL_OXYGEN_REQUIRED = 0.0000009;
    // The possibility that an animal may be infected by a disease.
    protected static final double INFECTION_RATE = 1;
    // The possibility an animal may die of a disease.
    protected static final double MORTALITY_RATE = 1;
    // The steps an animal need to withstand in order to get immunity
    protected static final int stepStandNum = 3;

    // If the animal is infected by disease.
    private boolean isInfected;
    // If the animal is immuned from the diease.
    private boolean isImmuned;

    // Track the first step at which the animal is infected;
    protected int infectionStartStep;

    // total population that is die of disease.
    public static int populationDieOfDisease = 0;

    public Animal(Field field, Location location){
        super(field, location);
        sex = (int)(Math.round(Math.random()));
        isInfected = false;
        isImmuned = false;
        // Track the first step at which the animal is infected;
        infectionStartStep = 0;

    }

    /**
     * get the gender of an animal.
     * @return sex  0 = female and 1 = male.
     */
    public int getSex(){
        return sex;
    }

    /**
     * Every animal have different gender. and the implementation of this method is at its subclass.
     * 
     */
    public abstract boolean encounterWithDiffSex();

    public abstract Location search(Disease disease, int step);

    /**
     * identify whether a creature need to sleep
     * 
     * @param atDayTime true if it is at day time false if it is at night time.
     * @return true if currently it is night.
     */
    public boolean needSleep(boolean atDayTime){
        return !atDayTime;
    }

    
    /**
     * get whether an animal is infected.
     * 
     * @return true if an animal is infected, false otherwise. 
     */
    public boolean getIsInfected(){
        return isInfected;
    }

    /**
     * get if an animal is immuned.
     * 
     * @return true if an animal is immuned, false otherwise.
     */
    public boolean getIsImmuned(){
        return isImmuned;
    }

    /**
     * set an animal to be infected.
     * @param isInfected 
     */
    public void setIsInfected(boolean isInfected){
        this.isInfected = isInfected;
    }

    /**
     * set an animal to be immuned.
     * @param isImmuned 
     */
    public void setIsImmuned(boolean isImmuned){
        this.isImmuned = isImmuned;
    }

    /**
     *  Make an animal infected while the disease exists.
     *  
     *  @param disease disease 
     *  @param step current step.
     */
    protected void makeInfected(Disease disease, int step){
        if((!this.getIsImmuned()) && Randomizer.getRandom().nextDouble() <= disease.INFECTION_RATE)
            setIsInfected(true);  

        //if the animal is infected in current step, record its start step.
        if(getIsInfected() && infectionStartStep == 0)
            infectionStartStep = step;
    }

    /**
     * give the animal immunity while condition is met.
     * @param disease disease.
     * @param step int step.
     */
    protected void ifCanGrantImmunity(Disease disease, int step){
        // if an animal is infected, it may die. Otherwise assume it gets immuntity from that disease.
        if(getIsInfected() && !getIsImmuned()){
            if(step-infectionStartStep >= disease.NUMBER_OF_STEP_TO_WITHSTAND){
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

    protected boolean dieOfInfection(Disease disease){
        // if an animal is infected, it may die. Otherwise assume it gets immuntity from that disease.
        if(getIsInfected() && !getIsImmuned()){
            if(Randomizer.getRandom().nextDouble() <= disease.MORTALITY_RATE  ){
                setDead();
                populationDieOfDisease++;
                return true;
            }
        }
        return false;
    }

    
}
