/**
 * Main Class.
 *
 * <P>Main Class to run the GuessNumber genetic Algorithm</P>
 *
 * @author Avidh Bavkar [avidhbavkar@gmail.com]
 * @version 1.0
 * @since   1.0
 */
public class Main {
    public static void main(String[] args){

        //initializes a population with a size and a minimum and maximum bound where the target number lies within
        Population pop = new Population(10, 0, 1000);
        //sets the target number. The purpose of this Algorithm is to converge and find this number.
        pop.setTarget(254);
        //print out the initial population (generated randomly)
        System.out.print(pop + "\n\n\n");

        int count = 0;
        while (!pop.hasFound()){
            //while the target has not been found:
            //iterate the count of how many iterations the algorithm takes to converge.
            count ++;

            //generate a mating pool of given size.
            pop.generateMatingPool(6);

            //from this mating pool, generate a new population of a given size.
            pop.reproduce(10, 2);

            //introduce a small chance of mutation into the population to avoid local minimums.
            pop.mutate(0.1, 0.2);

            //sleep for a short amount of time.
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //print out the current population
            System.out.print(pop + "\n\n\n");
        }
        //reveal the number of iterations the algorithm took to converge.
        System.out.println("Iterations: " + count);
    }
}
