import java.util.ArrayList;
import java.util.List;
import static java.lang.Math.pow;

/**
 * Class responsible for generating the simulation's terrain
 */
public class TerrainGenerator {

    /**
     * List containing the numbers used to distinguish the type of terrain. <br>
     * terrain types:<br>
     * 0 - plain, no effects<br>
     * 1 - desert, stamina -= 1 (min 1)<br>
     * 2 - forest, range = 1<br>
     * 3 - mountains, range += 1 (not applied for knife)<br>
     */

    static List<List<Integer>> terrainMap = new ArrayList<>();

    private static final double scaler = 0.04; //number used to scale the coordinates so that they create a better perlin noise

    /**
     * list containing randomly arranged numbers from 0 to 255, used to make random gradients used to generate the perlin noise
     */
    private static ArrayList<Integer> permutation = new ArrayList<>();

    /**
     * Method returning the value used to determine what type of the terrain should be at given coordinates
     * @param x given x coordinate
     * @param y given y coordinate
     */
    private static double perlinNoise(double x, double y) {
        permutationGenerator();
        //x0 and y0 are the coordinates of the square in which are given coordinates
        int x0 = (int) x;
        int y0 = (int) y;
        //random gradient are generated for every corner of the square chosen earlier
        int[] gradient1 = gradientGenerator(x0, y0);
        int[] gradient2 = gradientGenerator(x0, y0 + 1);
        int[] gradient3 = gradientGenerator(x0 + 1, y0);
        int[] gradient4 = gradientGenerator(x0 + 1, y0 + 1);
        //dot products are calculated for every gradient and vector from the corner to the given coordinates
        double dot1 = dotProductCalc(x0, y0, x, y, gradient1);
        double dot2 = dotProductCalc(x0, y0 + 1, x, y, gradient2);
        double dot3 = dotProductCalc(x0 + 1, y0, x, y, gradient3);
        double dot4 = dotProductCalc(x0 + 1, y0 + 1, x, y, gradient4);
        //weights of interpolation are determined with used of perlin noise's fade to make changes smoother
        //fade function used in the program is the same as the fade function used in improved perlin noise implementation:
        //6*t^5-15*t^4+10*t^3
        double u = fade(x - x0);
        double v = fade(y - y0);
        //dot products are interpolated with use of weight u and v
        double i1 = interpolate(dot1, dot3, u);
        double i2 = interpolate(dot2, dot4, u);
        return interpolate(i1, i2, v);
    }

    /**
     * Method used to generate the random gradients
     */
    private static int[] gradientGenerator(int x, int y) {
        int[] gradient = new int[2];
        //number from the permutation list is chosen based on given coordinates and one of the four possible gradients is chosen
        int perm = permutation.get((permutation.get(x) + y) % 255);
        switch((int) (perm % 4)) {
            case 0:
                gradient[0] = 0;
                gradient[1] = 1;
                break;
            case 1:
                gradient[0] = 0;
                gradient[1] = -1;
                break;
            case 2:
                gradient[0] = 1;
                gradient[1] = 0;
                break;
            case 3:
                gradient[0] = -1;
                gradient[1] = 0;
                break;
        }
        return gradient;
    }

    /**
     * Method used to calculate the dot product of the given vector and gradient
     */
    private static double dotProductCalc(int xi, int yi, double x, double y, int[] gradient) {
        double dx = x - xi;
        double dy = y - yi;
        return dx * gradient[0] + dy * gradient[1];
    }

    /**
     * Method used to apply the fade function to the weight for interpolation
     */
    private static double fade(double x) {
        return pow(x, 3) * (6 * x * x - 15 * x + 10);
    }

    /**
     * Method used to calculate the interpolation of two dot products
     */
    private static double interpolate(double dot1, double dot2, double weight) {
        return (dot2 - dot1) * weight + dot1;
    }

    /**
     * method used to generate the random permutation
     */
    private static void permutationGenerator() {
        ArrayList<Integer> numbersList = new ArrayList<>();
        for(int i = 0; i < 256; i++) {
            numbersList.add(i);
        }
        for(int i = 0; i < 256; i++) {
            int number = numbersList.get((int) (Math.random() * numbersList.size()));
            permutation.add(number);
            numbersList.remove(numbersList.indexOf(number));
        }
    }

    /**
     * Method used to fill the terrainMap list with numbers used to distinguish terrain types
     * @param size size of the simulation's board
     */
    public static void terrainGenerator(int size) {
        for(int y = 0; y < size; y++) {
            terrainMap.add(new ArrayList<>());
            for(int x = 0; x < size; x++) {
                double value = perlinNoise(x * scaler, y * scaler);
                if(value < -0.2) {
                    terrainMap.get(y).add(0);
                }
                else if(value < 0) {
                    terrainMap.get(y).add(1);
                }
                else if(value < 0.2) {
                    terrainMap.get(y).add(2);
                }
                else {
                    terrainMap.get(y).add(3);
                }
            }
        }
        permutation.clear();
    }

    /**
     * Method used to create and change the size of the closing zone that will deal damage to NPCs
     * @param size size of the simulation's board
     * @param centerX X coordinate of the closing zone's center
     * @param centerY Y coordinate of the closing zone's center
     * @param turn numbered turn of the simulation
     */
    public static void ShrinkZone(int size,int centerX,int centerY,int turn) {
        double radius = size - (turn-1);
        //if a tile is outside a circular area with the above radius set it's terrain value to 4 - zone
        for(int y = 0; y < size; y++) {
            for(int x = 0; x < size; x++) {
                if(Logic.distanceCalc(x,y,centerX,centerY)>=radius){
                    terrainMap.get(y).set(x,4);
                }
            }
        }
    }
}
