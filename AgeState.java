/**
 * Mutable age-related state for an organism.
 *
 * @version 26/02/2022
 */
public class AgeState
{
    private int age;
    private int maxAge;

    public int getAge()
    {
        return age;
    }

    public void setAge(int age)
    {
        this.age = age;
    }

    public int getMaxAge()
    {
        return maxAge;
    }

    public void setMaxAge(int maxAge)
    {
        this.maxAge = maxAge;
    }
}
