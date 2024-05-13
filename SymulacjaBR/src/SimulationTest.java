import java.util.ArrayList;
import java.util.Arrays;

import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

//Na razie całość trzymam w osobnej klasie i jest zhardcodowana, bo tak mi wygodniej testować
//Wszystkie outputy w konsoli tylko do testów
//wkurza mnie, że za każdym razem jak szukam celu to robię nową praktycznie taką samą pętle więc kiedyś to spróbuje poprawić
public class SimulationTest {
    public static void simulation(/*NPC's object???*/) {
        //TODO: it should be using objects
        //Staty NPC do testu:
        int x = 1;
        int y = 1;
        int HP = 50;
        int maxHP = 100;
        int stamina = 2;
        //Staty WPN do testu:
        int DMG = 20;
        int range = 2;
        int quality = 1;
        //Tablice do testu:
        int[][] npc = {{x, y, HP}, {7, 8, 100}, {3, 7, 54}};
        //TODO: decide if we want to store current HP in an array with coordinates
        int[][] wpn = {{3, 3, 2}, {2, 3, 1}, {1, 9, 3}};
        int[][] heal = {{2, 2}, {6, 9}, {10, 10}};
        //Dane potrzebne do pracy programu:
        int targetX = -1; //współrzędna x celu
        int targetY = -1; //współrzędna y celu
        double targetDistance = 999; //odległość od celu
        int npcIndex = -1; //Numer aktualnie sterowanego NPC w tabeli npc lub linijki w pliku potem
        int targetIndex = -1; //Numer celu do strzału w tabeli npc lub linijki w pliku potem
        boolean actionTaken = false;
        //loop to find the index of current NPC
        for(int i = 0; i < npc.length; i++){
            if(x == npc[i][0] && y == npc[i][1]){
                npcIndex = i;
                break;
            }
        }
        //sprawdzam czy HP mniejsze od 50% i jeśli tak to szuka najbliższej apteczki
        if((double) HP / maxHP < 0.5 && heal.length > 0) {
            //the loop finding the closest aid kit if HP under 50% and saving its coordinates
            //TODO: prevent it from looking for aid kit if there's none
            for(int i = 0; i < heal.length; i++) {
                double distance = distanceCalc(heal[i][0], heal[i][1], x, y);
                System.out.println("Sprawdzany: " + heal[i][0] + " " + heal[i][1] + " " + distance);
                if(distance > 0 && distance < targetDistance) {
                    targetDistance = distance;
                    targetX = heal[i][0];
                    targetY = heal[i][1];
                    targetIndex = i;
                }
            }
            System.out.println("Wybrana apteczka: " + targetX + " " + targetY + " " + targetDistance);
            //calling the method to move the target in direction of target
            for(int i = 1; i <= stamina; i++) {
                npc = movement(targetX, targetY, x, y, npc, npcIndex);
                x = npc[npcIndex][0];
                y = npc[npcIndex][1];
                System.out.println(x + " " + y);
                if(x == targetX && y == targetY) {
                    heal = itemRemover(heal, targetIndex);
                    System.out.println(Arrays.deepToString(heal));
                    break;
                }
                else{
                    for(int j = 0; i < wpn.length; i++) {
                        System.out.println("Sprawdzany: " + wpn[j][0] + " " + wpn[j][1]);
                        if (x == wpn[j][0] && y == wpn[j][1]) {
                            wpn = itemRemover(wpn, j);
                            System.out.println(Arrays.deepToString(wpn));
                            actionTaken = true;
                            break;
                        }
                    }
                }
                if(actionTaken) {
                    break;
                }
            }
        }
        //if the HP level of the NPC is higher than 50% of maximal value program check if there's an enemy in attack range
        else {
            boolean inRange = false; //true if there is an enemy in range, otherwise false
            int targetHP = 999; //HP points of current target
            //loop checks if there's an enemy in range and saves the coordinates of the one with the list HP points
            for(int i = 0; i < npc.length; i++) {
                double distance = distanceCalc(npc[i][0], npc[i][1], x, y);
                System.out.println("Sprawdzany NPC: " + npc[i][0] + " " + npc[i][1] + " " + distance);
                if(distance > 0 && npc[i][2] < targetHP && distance <= range) {
                    targetDistance = distance;
                    targetHP = npc[i][2];
                    targetX = npc[i][0];
                    targetY = npc[i][1];
                    targetIndex = i;
                    inRange = true;
                }
            }
            System.out.println(targetX + " " + targetY + " " + targetDistance);
            //jeśli znalazło cel to wywołuje motodę odpowiedzialną za zadawanie obrażeń
            if(inRange == true) {
                damageDealer(DMG, npc, targetIndex);
            }
            //if there's no one in range it looks for the closest target of travel (another NPC or better weapon)
            else {
                //the loop finding the closest NPC and saving its coordinates
                for(int i = 0; i < npc.length; i++) {
                    double distance = distanceCalc(npc[i][0], npc[i][1], x, y);
                    System.out.println("Sprawdzany: " + npc[i][0] + " " + npc[i][1] + " " + distance);
                    if(distance > 0 && distance < targetDistance) {
                        targetDistance = distance;
                        targetX = npc[i][0];
                        targetY = npc[i][1];
                    }
                }
                //this loop tries to find a weapon that is better than the one wielded by NPC and is closer than the closest enemy
                //if it finds such weapon it saves its coordinates
                //TODO: it could also be prevent from going through the loop if there's no weapon
                System.out.println("Wybrany NPC: " + targetX + " " + targetY + " " + targetDistance);
                for(int i = 0; i < wpn.length; i++) {
                    if(wpn[i][2] > quality) {
                        double distance = distanceCalc(wpn[i][0], wpn[i][1], x, y);
                        System.out.println("Sprawdzany: " + wpn[i][0] + " " + wpn[i][1] + " " + distance);
                        if (distance > 0 && distance < targetDistance) {
                            targetDistance = distance;
                            targetX = wpn[i][0];
                            targetY = wpn[i][1];
                        }
                    }
                }
                System.out.println("Wybrana broń: " + targetX + " " + targetY + " " + targetDistance);
                //calling the method to change the coordinates of the NPC in targets direction
                for(int i = 1; i <= stamina; i++) {
                    npc = movement(targetX, targetY, x, y, npc, npcIndex);
                    x = npc[npcIndex][0];
                    y = npc[npcIndex][1];
                    System.out.println(x + " " + y);
                    for(int j = 0; j < wpn.length; j++) {
                        System.out.println("Sprawdzany: " + wpn[j][0] + " " + wpn[j][1]);
                        if (x == wpn[j][0] && y == wpn[j][1]) {
                            wpn = itemRemover(wpn, j);
                            System.out.println(Arrays.deepToString(wpn));
                            actionTaken = true;
                            break;
                        }
                    }
                    if(actionTaken) {
                        break;
                    }
                    for(int j = 0; j < heal.length; j++) {
                        System.out.println("Sprawdzany: " + heal[j][0] + " " + heal[j][1]);
                        if (x == heal[j][0] && y == heal[j][1]) {
                            heal = itemRemover(heal, j);
                            System.out.println(Arrays.deepToString(heal));
                            actionTaken = true;
                            break;
                        }
                    }
                    if(actionTaken) {
                        break;
                    }
                    //TODO: checking if an item is at this coordinates and should be raised
                }
            }
        }
    }
    //the method to calculate the distance between to point on the map
    //TODO: check if it can be simplified
    public static double distanceCalc(int targetX, int targetY, int x, int y) {
        double distance = sqrt(abs(x - targetX) * abs(x - targetX) + abs(y - targetY) * abs(y - targetY));
        return  distance;
    }
    //TODO: prevent NPC from moving to the space occupied by the other NPC
    public static int[][] movement(int targetX, int targetY, int x, int y, int[][] npc, int npcIndex) {
        //targets coordinates are saved as targetX, targetY
        //proponuje, żeby NPC mogli się poruszać po skosie, bo wtedy ścieżki, po których się będą poruszać będą bardziej naturalne
        System.out.println("Poruszam się");
        if(targetX == x && targetY < y) {
            npc[npcIndex][1]--;
        }
        else if(targetX > x && targetY < y) {
            npc[npcIndex][0]++;
            npc[npcIndex][1]--;
        }
        else if(targetX > x && targetY == y) {
            npc[npcIndex][0]++;
        }
        else if(targetX > x && targetY > y) {
            npc[npcIndex][0]++;
            npc[npcIndex][1]++;
        }
        else if(targetX == x && targetY > y) {
            npc[npcIndex][1]++;
        }
        else if(targetX < x && targetY > y) {
            npc[npcIndex][0]--;
            npc[npcIndex][1]++;
        }
        else if(targetX < x && targetY == y) {
            npc[npcIndex][0]--;
        }
        else if(targetX < x && targetY < y) {
            npc[npcIndex][0]--;
            npc[npcIndex][1]--;
        }
        return npc;
    }
    public static void damageDealer(int DMG, int[][] npc, int indexTarget){
        //zamiast koordynatów jest targetIndex jako index w tabeli/linijka w pliku
        System.out.println("Atakuje npc z indexem: " + indexTarget);
        npc[indexTarget][2] -= DMG;
        //TODO: remove npc from array with index
    }
    public static int[][] itemRemover(int[][] item, int indexTarget){
        int[][] itemRemover = new int[item.length - 1][];
        for (int i = 0, k = 0; i < item.length; i++) {
            if (i != indexTarget) {
                itemRemover[k] = item[i];
                k++;
            }
        }
        System.out.println(Arrays.deepToString(itemRemover));
        return itemRemover;
    }
    //main, którego używam tylko do testów i się go potem wyrzuci
    public static void main(String[] args) {
        simulation();
    }
}
