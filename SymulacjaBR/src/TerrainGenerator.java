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

    private static final double scaler = 0.05;

    private static final int[] permutation= { 151, 160, 137,  91,  90,  15, 131,  13, 201,  95,  96,  53, 194, 233,   7, 225,
            140,  36, 103,  30,  69, 142,   8,  99,  37, 240,  21,  10,  23, 190,   6, 148,
            247, 120, 234,  75,   0,  26, 197,  62,  94, 252, 219, 203, 117,  35,  11,  32,
            57, 177,  33,  88, 237, 149,  56,  87, 174,  20, 125, 136, 171, 168,  68, 175,
            74, 165,  71, 134, 139,  48,  27, 166,  77, 146, 158, 231,  83, 111, 229, 122,
            60, 211, 133, 230, 220, 105,  92,  41,  55,  46, 245,  40, 244, 102, 143,  54,
            65,  25,  63, 161,   1, 216,  80,  73, 209,  76, 132, 187, 208,  89,  18, 169,
            200, 196, 135, 130, 116, 188, 159,  86, 164, 100, 109, 198, 173, 186,   3,  64,
            52, 217, 226, 250, 124, 123,   5, 202,  38, 147, 118, 126, 255,  82,  85, 212,
            207, 206,  59, 227,  47,  16,  58,  17, 182, 189,  28,  42, 223, 183, 170, 213,
            119, 248, 152,   2,  44, 154, 163,  70, 221, 153, 101, 155, 167,  43, 172,   9,
            129,  22,  39, 253,  19,  98, 108, 110,  79, 113, 224, 232, 178, 185, 112, 104,
            218, 246,  97, 228, 251,  34, 242, 193, 238, 210, 144,  12, 191, 179, 162, 241,
            81,  51, 145, 235, 249,  14, 239, 107,  49, 192, 214,  31, 181, 199, 106, 157,
            184,  84, 204, 176, 115, 121,  50,  45, 127,   4, 150, 254, 138, 236, 205,  93,
            222, 114,  67,  29,  24,  72, 243, 141, 128, 195,  78,  66, 215,  61, 156, 180 };

    private static double perlinNoise(double x, double y) {
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
        int perm = permutation[(permutation[x] + y) % 255];
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

    public static void terrainGenerator(int size) {
        for(int y = 0; y < size; y++) {
            terrainMap.add(new ArrayList<>());
            for(int x = 0; x < size; x++) {
                double value = perlinNoise(x * scaler, y * scaler);
                System.out.print(value + " ");
                if(value < -0.25) {
                    terrainMap.get(y).add(0);
                }
                else if(value < 0) {
                    terrainMap.get(y).add(1);
                }
                else if(value < 0.25) {
                    terrainMap.get(y).add(2);
                }
                else {
                    terrainMap.get(y).add(3);
                }
                //System.out.print(terrainMap.get(y).get(x) + " ");
            }
            System.out.print("\n");
        }
    }
}
