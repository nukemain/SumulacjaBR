import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

public class SimulationTest {
    public static void simulation(/*obiekt NPC*/) {
        //Staty NPC do testu:
        int x = 1;
        int y = 1;
        int HP = 49;
        int maxHP = 100;
        int stamina = 1;
        //Staty WPN do testu:
        int DMG = 20;
        int range = 2;
        int quality = 1;
        //Tablice do testu:
        int[][] npc = {{1, 1}, {6, 4}, {9, 8}};
        int[][] wpn = {{5, 9, 2}, {2, 3, 1}, {1, 9, 3}};
        int[][] heal = {{8, 3}, {3, 7}, {10, 10}};
        //Dane celu podróży:
        int targetX = -1;
        int targetY = -1;
        double targetDistance = 999;

        if(HP < 50) {
            for(int i = 0; i < heal.length; i++) {
                double distance = distanceCalc(heal[i][0], heal[i][1], x, y);
                System.out.println(heal[i][0] + " " + heal[i][1] + " " + distance);
                if(distance > 0 && distance < targetDistance) {
                    targetDistance = distance;
                    targetX = heal[i][0];
                    targetY = heal[i][1];
                }
            }
            System.out.println(targetX + " " + targetY + " " + targetDistance);
        }
    }

    public static double distanceCalc(int targetX, int targetY, int x, int y) {
        int distanceX = abs(x - targetX);
        int distanceY = abs(y - targetY);
        double distance = sqrt(distanceX * distanceX + distanceY * distanceY);
        return  distance;
    }
    public static void main(String[] args) {
        simulation();
    }
}
