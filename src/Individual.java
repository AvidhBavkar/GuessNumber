/**
 * Individual Class.
 *
 * <P>Represents an Individual Member of the overall Population in the GuessNumber genetic algorithm</P>
 *
 * @author Avidh Bavkar [avidhbavkar@gmail.com]
 * @version 1.0
 * @since   1.0
 */
public class Individual {

    /**
     * The value (DNA) that this member holds
     */
    private double val;

    /**
     * Constructor.
     *
     * @param val the value or DNA of this individual.
     */
    public Individual(double val){
        this.val = val;
    }

    /**
     * getVal Method.
     *
     * @return the value or DNA of this Individual
     */
    public double getVal(){
        return val;
    }
}
