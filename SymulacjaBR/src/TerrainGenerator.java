import java.util.ArrayList;
import java.util.List;

public class TerrainGenerator {

    // terrain types:
    // 0 - plain, no effects
    // 1 - desert, stamina -= 1 (min 1)
    // 2 - forest, range = 1
    // 3 - mountains, range += 1 (not applied for knife)
    static List<List<Integer>> terrainMap = new ArrayList<>();

    public static void terrainGenerator(int size) {
        for(int y = 0; y < size; y++) {
            terrainMap.add(new ArrayList<>());
            for(int x = 0; x < size; x++) {
                terrainMap.get(y).add((int)(Math.random() * 4));
                System.out.print(terrainMap.get(y).get(x) + " ");
            }
            System.out.print("\n");
        }
    }
}
