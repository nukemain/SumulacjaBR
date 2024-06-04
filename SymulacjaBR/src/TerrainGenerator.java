import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.pow;

public class TerrainGenerator {

    // terrain types:
    // 0 - plain, no effects
    // 1 - desert, stamina -= 1 (min 1)
    // 2 - forest, range = 1
    // 3 - mountains, range += 1 (not applied for knife)
    static List<List<Integer>> terrainMap = new ArrayList<>();

    private static final double scaler = 0.04;

    private static ArrayList<Integer> permutation = new ArrayList<>();

    private static double perlinNoise(double x, double y) {
        permutationGenerator();
        int x0 = (int) x;
        int y0 = (int) y;
        int[] gradient1 = gradientGenerator(x0, y0);
        int[] gradient2 = gradientGenerator(x0, y0 + 1);
        int[] gradient3 = gradientGenerator(x0 + 1, y0);
        int[] gradient4 = gradientGenerator(x0 + 1, y0 + 1);
        double dot1 = dotProductCalc(x0, y0, x, y, gradient1);
        double dot2 = dotProductCalc(x0, y0 + 1, x, y, gradient2);
        double dot3 = dotProductCalc(x0 + 1, y0, x, y, gradient3);
        double dot4 = dotProductCalc(x0 + 1, y0 + 1, x, y, gradient4);
        double u = fade(x - x0);
        double v = fade(y - y0);
        double i1 = interpolate(dot1, dot3, u);
        double i2 = interpolate(dot2, dot4, u);
        return interpolate(i1, i2, v);
    }

    private static int[] gradientGenerator(int x, int y) {
        int[] gradient = new int[2];
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

    private static double dotProductCalc(int xi, int yi, double x, double y, int[] gradient) {
        double dx = x - xi;
        double dy = y - yi;
        return dx * gradient[0] + dy * gradient[1];
    }

    private static double fade(double x) {
        return pow(x, 3) * (6 * x * x - 15 * x + 10);
    }

    private static double interpolate(double dot1, double dot2, double weight) {
        return (dot2 - dot1) * weight + dot1;
    }

    private static void permutationGenerator() {
        ArrayList<Integer> numbersList = new ArrayList<>();
        for(int i = 0; i < 256; i++) {
            numbersList.add(i);
        }
        for(int i = 0; i < 256; i++) {
            int number = numbersList.get((int) (Math.random() * numbersList.size()));
            //System.out.print(number + " ");
            permutation.add(number);
            numbersList.remove(numbersList.indexOf(number));
        }
    }

    public static void terrainGenerator(int size) {
        for(int y = 0; y < size; y++) {
            terrainMap.add(new ArrayList<>());
            for(int x = 0; x < size; x++) {
                double value = perlinNoise(x * scaler, y * scaler);
                //System.out.print(value + " ");
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
                //System.out.print(terrainMap.get(y).get(x) + " ");
            }
        }
        permutation.clear();
    }
}
