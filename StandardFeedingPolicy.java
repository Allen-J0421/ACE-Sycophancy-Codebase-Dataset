import java.util.Random;

/**
 * Default feeding behavior shared by animals and eagles.
 */
public final class StandardFeedingPolicy implements FeedingPolicy
{
    private static final Random rand = Randomizer.getRandom();
    private static final RemovalPolicy REMOVAL_POLICY = new StandardRemovalPolicy();

    @Override
    public void feed(Animal consumer, Organism prey)
    {
        if (prey.isDiseased()
                && prey.getDisease().getDiseaseType() == DiseaseType.FOODBORNE
                && prey.getDisease().getPropagationRate() <= rand.nextDouble())
        {
            consumer.setDisease(prey.getDisease());
        }

        if(prey.isAlive())
        {
            REMOVAL_POLICY.remove(prey);
            int newFoodLevel = consumer.foodLevel + ((Edible) prey).getFoodValue();
            consumer.foodLevel = Math.min(newFoodLevel, consumer.MAX_FOOD_LEVEL());
        }
    }
}
