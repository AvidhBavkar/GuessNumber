import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Population Class.
 *
 * <p>Main Class of the GuessNumber Genetic Algorithm.</p>
 *
 * @author Avidh Bavkar [avidhbavkar@gmail.com]
 * @version 1.0
 * @since   1.0
 */
public class Population {
    /**
     * Algorithm Target
     */
    private double TARGET = 0;

    /**
     * Holds the Individuals that make up the Population
     */
    private ArrayList<Individual> population;

    /**
     * Has any one individual become equivalent to the target
     */
    private static boolean hasFound = false;

    /**
     * Minimum expected target
     */
    private int lowBound;

    /**
     * Maximum expected target
     */
    private int highBound;


    /**
     * Constructor.
     *
     * <p>Constructs a new Population of Individuals with randomized DNA within a certain target range.</p>
     *
     * @param populationSize initial size of the population.
     * @param minBound the lower bound for the range in which to look for the target
     * @param maxBound the upper bound of the range in which to look for the target
     */
    public Population(int populationSize, int minBound, int maxBound) {
        population = new ArrayList<>(); //initialize the population

        for (int i = 0; i < populationSize; i++) {
            //populate the arrayList with randomized Individuals between the minBound and the maxBound
            population.add(new Individual(ThreadLocalRandom.current().nextDouble(minBound, maxBound + 1)));
        }

        //set the minimum and maximum bounds
        this.lowBound = minBound;
        this.highBound = maxBound;
    }


    /**
     * Set Target Method.
     *
     * <p>Sets the Algorithm's target</p>
     * @param target the new goal of the Algorithm
     */
    public void setTarget(double target) {
        TARGET = target;
    }


    /**
     * Calculate Individual Fitness Method
     *
     * <p>Calculates the fitness (distance from the target) of any Individual. As such, lower numbers indicate higher
     *    fitness</p>
     *
     * @param indiv the individual to evaluate
     * @return the absolute difference from the target. Lower is more fit.
     */
    private double calculateIndividualFitness(Individual indiv) {
        //return the absolute difference between the individual and the target
        return Math.abs(TARGET - indiv.getVal());
    }


    /**
     * Calculate Relative Fitness method.
     *
     * <p> Calculates the fitness of an individual in respect to the maximum fitness of the population at this point in
     * time. Useful for ranking.</p>
     *
     * @param indiv the Individual to evaluate.
     * @return the fitness of the individual on a scale from 0-1 relative to the maximum observed fitness in the
     *         population. Higher numbers indicate higher fitness, a value of 1 indicating that the Individual is either
     *         the most fit or tied for most fit in the population.
     */
    private double calculateRelativeFitness(Individual indiv) {
        //return the LowestFitness in the population (most fit fitness) divided by the individual fitness
        return getLowestFitness() / calculateIndividualFitness(indiv);
    }


    /**
     * Get Lowest Fitness method.
     *
     * <p> Returns the fitness of the most fit individual in the population. Recall lower individual fitness values
     * indicate a more fit Individual </p>
     *
     * @return the fitness of the most fit individual in the population.
     */
    private double getLowestFitness() {
        //initialize a lowest fitness at the maximum value a double can store (so any other number is less than it)
        double minFitness = Double.MAX_VALUE;

        for (Individual pop : population) {
            //iterate through the population and find the most fit Individual
            if (calculateIndividualFitness(pop) < minFitness)
                minFitness = calculateIndividualFitness(pop);
        }
        //return the fitness of the fittest individual
        return minFitness;
    }


    /**
     * Generate Mating Pool method.
     *
     * <p> Introduces some "Natural Selection" that eliminates low fitness members from the population</p>
     *
     * @param size the size to limit the population too
     * @return the population after selection
     */
    public ArrayList<Individual> generateMatingPool(int size) {

        //Sort a new array based on the relative fitness of the Individuals
        ArrayList<Individual> matingPool = sortArray();

        for (int i = matingPool.size() - 1; i > size - 1; i--) {
            //iterate backwards through the sorted array, removing elements until the array is the correct size
            matingPool.remove(i);
        }

        //update the population array.
        population = matingPool;
        return matingPool; //return the sorted, selected arrayList ready for mating
    }

    /**
     * Sort Array method.
     *
     * <p> Uses an Insertion Sort Algorithm to sort the population based on relative fitness from most fit to least</p>
     *
     * @return the sorted population ArrayList.
     */
    private ArrayList<Individual> sortArray() {
        //A temporary Individual useful for switching values in an array
        Individual temp;

        for (int playHead = 0; playHead < population.size(); playHead++) {
            //iterate a "playhead" through the array
            for (int scanner = playHead; scanner > 0; scanner--){
                //from this "playhead", iterate a "scanner" backwards through the array.
                if (calculateRelativeFitness(population.get(scanner)) > calculateRelativeFitness(population.get(scanner - 1))){
                    //if the fitness of the indiv at the scanner is greater than the one before it, switch them:

                    //set the indiv at the scanner to the temp for holding
                    temp = population.get(scanner);
                    //replace the value at the scanner with the one before it
                    population.set(scanner, population.get(scanner - 1));
                    //grab the original value at the scanner and put it into the slot before it
                    population.set(scanner-1, temp);
                }
            }
        }
        return population; // return the sorted population
    }


    /**
     * Reproduce method.
     *
     * <p>Replaces the population with a new generation, the offspring of the current population.</p>
     *
     * @param offSpringSize the desired size of the offspring population.
     * @param matingPartners the number of partners used to make one offspring.
     * @return the offspring population.
     */
    public ArrayList<Individual> reproduce(int offSpringSize, int matingPartners){

        //make a new array of Individuals to hold the offspring
        ArrayList<Individual> offSpring = new ArrayList<>();

        //initialize some temporary variables
        int tempRandomIndex, tempSum;

        for (int i = 0; i < offSpringSize; i++){
            //until the offSpring population reaches the desired size.
            tempSum = 0; //zero the sum used to average.

            //sum the values from the specified number of random selected parents.
            for (int partners = 0; partners < matingPartners; partners++){
                tempRandomIndex = ThreadLocalRandom.current().nextInt(0, population.size());
                tempSum += population.get(tempRandomIndex).getVal();
            }

            //add a new child to the offSpring array that's "DNA" is the average of all of its parents
            offSpring.add(new Individual(tempSum / matingPartners));
        }

        //after breeding is finished replace the parents with their children.
        population = offSpring;

        hasFound(); //check if any of the children have the target DNA
        return offSpring; //return the offSpring array.
    }


    /**
     * Mutate method.
     *
     * <p> Mutate some members of the population to avoid local minimums</p>
     *
     * @param randomChance chance (0-1) that a member of the population will be set to a completely random value between
     *                     the upper and lower bounds.
     * @param incrementChance chance (0-1) that a member of the population will be incremented or decremented by one.
     * @return the population after mutation has taken place.
     */
    public ArrayList<Individual> mutate(double randomChance, double incrementChance){
        //iterate through the population
        for (int i = 0; i < population.size(); i++){

            //if a random number is less than or equal to the random chance, replace that member of the population with
            //a completely random value between the low and high bounds
            if (Math.random() <= randomChance){
                population.set(i, new Individual(ThreadLocalRandom.current().nextDouble(lowBound, highBound + 1)));
            }

            //if a random number is less than or equal to the increment chance, increment that member of the population
            if (Math.random() <= incrementChance){
                population.set(i, new Individual(population.get(i).getVal() + 1));
            }

            //if a random number is less than or equal to the increment chance, decrement that member of the population
            if (Math.random() <= incrementChance){
                population.set(i, new Individual(population.get(i).getVal() - 1));
            }
        }

        hasFound(); //check if one of the mutants has the target DNA.
        return population; //return the mutated population.
    }


    /**
     * Has Found method.
     *
     * <p>sets a flag if any member of the population upon call has the target DNA</p>
     *
     * @return a flag that is set to true if the population contains the target DNA.
     */
    public boolean hasFound(){
        //iterate the population, if any element contains target DNA set the flag.
        for (Individual pop : population)
            if (pop.getVal() == TARGET)
                hasFound = true;
        //return this flag.
        return hasFound;
    }


    /**
     * To String Method.
     *
     * <p> Returns a human readable list of all the members of the population as well as their fitness and relative
     * fitness's</p>
     *
     *
     * @return human readable string of all of the members of the population and their respective fitness and relative
     *         fitness
     */
    @Override
    public String toString() {
        //formatter to format decimals to a reasonable size
        DecimalFormat formatter = new DecimalFormat("#00.000");

        //String to return. Include a header
        StringBuilder s = new StringBuilder("*****LISTING POPULATION ELEMENTS*****\n");
        for (int i = 0; i < population.size(); i++) {
            //iterate through the array and append to the String
            s.append("Value #").append(i).append("\t|Value: ").append(formatter.format(population.get(i).getVal())).
                    append("\t|Fitness: ").append(formatter.format(calculateIndividualFitness(population.get(i)))).
                    append("\t|Rel Fitness: ").append(formatter.format(calculateRelativeFitness(population.get(i)))).
                    append("\n");
        }
        //return the String
        return s.append("*****END LISTING POPULATION*****").toString();
    }
}
