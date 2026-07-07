
/**
 * Encapsulates the biological lifecycle state shared by all actors:
 * age and accumulated growth level. Held by composition inside Actor.
 */
public class BiologicalTrait
{
    private int age;
    private double growthLevel;

    public int getAge()
    {
        return age;
    }

    public void setAge(int age)
    {
        this.age = age;
    }

    public void incrementAge()
    {
        age++;
    }

    public double getGrowthLevel()
    {
        return growthLevel;
    }

    public void addGrowth(double value)
    {
        growthLevel += value;
    }
}
