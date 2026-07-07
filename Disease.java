/**
 * Immutable disease parameters consumed by Animal methods (infection rate,
 * mortality rate, immunity threshold). Lifecycle state and spread logic live
 * in DiseaseManager.
 *
 * @version 2022/03/02
 */
public class Disease
{
    // The possibility that an animal may be infected by a disease.
    public static final double INFECTION_RATE = 0.3;
    // The possibility an animal may die of a disease.
    public static final double MORTALITY_RATE = 0.05;
    // The steps an animal need to withstand in order to get immunity.
    public static final int NUMBER_OF_STEP_TO_WITHSTAND = 3;
}
