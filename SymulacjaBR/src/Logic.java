import java.util.*;

import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

public class Logic {
    static List<NPC> npcArray = new ArrayList<>();
    static List<Weapon> weaponsArray = new ArrayList<>();
    public static void main(String[] args)
    {
        //TODO: Na koniec jak już będzie śmigać dodać input od użytkownika w GUI.
        int NPCcount = 15;
        int sizeX=30;
        int sizeY=30;
        if(NPCcount>(sizeX-1)*(sizeY-1) - 1){
            System.out.println("Number of NPCs can not be higher than amount of fields.");
        }
        else{
            Symulacja(sizeX,sizeY,NPCcount);
        }
        decisionMaker(0);

    }

    static void Symulacja(int sizeX,int sizeY,int NPCcount){
        //TODO: Wczytywanie danych z pliku? - nie trzeba by było przekazywać miliona zmiennych do funkcji

        /*
            sizeX = rozmiar x planszy
            sizeY = rozmiar Y planszy
            NPCcount = ile agentów npc ma mieć symulacja
            WPNcount = ile sztuk broni ma zrespić symulacja
            //todo: przerebić ten komentarz na angielski jak już nie będzie potrebny
        */

        String[][] map = Spawning.createMap(sizeX,sizeY);
        //List<NPC> npcArray = new ArrayList<>();
        Spawning.spawnNPCs(sizeX,sizeY,NPCcount,map, npcArray);//Logic required for spawning NPC's
        //<Weapon> weaponsArray = new ArrayList<>();
        Spawning.spawnWeapons(map,sizeX,sizeY,NPCcount, weaponsArray);//Logic required for spawning weapons

        //tymcasowa pętla do drukowania tablic
        for(int y=0;y<sizeY;y++){
            for(int x=0;x<sizeX;x++){
                System.out.print(map[y][x]);
            }
            System.out.println();
        }
        System.out.println("---------------------------------");
        for (NPC npc : npcArray) {
            System.out.println(npc);
        }
        System.out.println("---------------------------------");
        for (Weapon weapon : weaponsArray) {
            System.out.println(weapon);
        }

    }
    public static void decisionMaker(int npcIndex) {
        //TODO: it should be using objects
        //Tablice do testu:
        /*int[][] npc = {{x, y, HP}, {7, 8, 100}, {3, 7, 54}};
        //TODO: decide if we want to store current HP in an array with coordinates
        int[][] wpn = {{3, 3, 2}, {2, 3, 1}, {1, 9, 3}};*/
        int[][] heal = {{2, 2}, {6, 9}, {10, 10}};
        //Dane potrzebne do pracy programu:
        int targetX = -1; //współrzędna x celu
        int targetY = -1; //współrzędna y celu
        double targetDistance = 999; //odległość od celu
        int targetIndex = -1; //Numer celu do strzału w tabeli npc lub linijki w pliku potem
        boolean actionTaken = false;
        //sprawdzam czy HP mniejsze od 50% i jeśli tak to szuka najbliższej apteczki
        if((double) npcArray.get(npcIndex).HP / npcArray.get(npcIndex).maxHP < 0.5 && heal.length > 0) {
            //the loop finding the closest aid kit if HP under 50% and saving its coordinates
            for(int i = 0; i < heal.length; i++) {
                double distance = distanceCalc(heal[i][0], heal[i][1], npcArray.get(npcIndex).posX, npcArray.get(npcIndex).posY);
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
            for(int i = 1; i <= npcArray.get(npcIndex).stamina; i++) {
                movement(targetX, targetY, npcArray.get(npcIndex).posX, npcArray.get(npcIndex).posY, npcIndex);
                System.out.println(npcArray.get(npcIndex).posX + " " + npcArray.get(npcIndex).posY);
                if(npcArray.get(npcIndex).posX == targetX && npcArray.get(npcIndex).posY == targetY) {
                    heal = itemRemover(heal, targetIndex);
                    System.out.println(Arrays.deepToString(heal));
                    break;
                }
                else{
                    for(int j = 0; j < weaponsArray.size(); j++) {
                        System.out.println("Sprawdzany: " + weaponsArray.get(j).posX + " " + weaponsArray.get(j).posY);
                        if (npcArray.get(npcIndex).posX == weaponsArray.get(j).posX && npcArray.get(npcIndex).posY == weaponsArray.get(j).posY) {
                            weaponsArray.remove(j);
                            System.out.println(weaponsArray);
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
            for(int i = 0; i < npcArray.size(); i++) {
                double distance = distanceCalc(npcArray.get(i).posX, npcArray.get(i).posY, npcArray.get(npcIndex).posX, npcArray.get(npcIndex).posY);
                System.out.println("Sprawdzany NPC: " + npcArray.get(i).posX + " " + npcArray.get(i).posY + " " + distance);
                if(distance > 0 && npcArray.get(i).HP < targetHP && distance <= npcArray.get(npcIndex).weapon.range) {
                    targetDistance = distance;
                    targetHP = npcArray.get(i).HP;
                    targetX = npcArray.get(i).posX;
                    targetY = npcArray.get(i).posY;
                    targetIndex = i;
                    inRange = true;
                }
            }
            System.out.println(targetX + " " + targetY + " " + targetDistance);
            //jeśli znalazło cel to wywołuje motodę odpowiedzialną za zadawanie obrażeń
            if(inRange == true) {
                damageDealer(npcArray.get(npcIndex).weapon.damage, targetIndex);
            }
            //if there's no one in range it looks for the closest target of travel (another NPC or better weapon)
            else {
                //the loop finding the closest NPC and saving its coordinates
                for(int i = 0; i < npcArray.size(); i++) {
                    double distance = distanceCalc(npcArray.get(i).posX, npcArray.get(i).posY, npcArray.get(npcIndex).posX, npcArray.get(npcIndex).posY);
                    System.out.println("Sprawdzany: " + npcArray.get(i).posX + " " + npcArray.get(i).posY + " " + distance);
                    if(distance > 0 && distance < targetDistance) {
                        targetDistance = distance;
                        targetX = npcArray.get(i).posX;
                        targetY = npcArray.get(i).posY;
                    }
                }
                //this loop tries to find a weapon that is better than the one wielded by NPC and is closer than the closest enemy
                //if it finds such weapon it saves its coordinates
                //TODO: it could also be prevent from going through the loop if there's no weapon
                System.out.println("Wybrany NPC: " + targetX + " " + targetY + " " + targetDistance);
                for(int i = 0; i < weaponsArray.size(); i++) {
                    if(weaponsArray.get(i).quality > npcArray.get(npcIndex).weapon.quality) {
                        double distance = distanceCalc(weaponsArray.get(i).posX, weaponsArray.get(i).posY, npcArray.get(npcIndex).posX, npcArray.get(npcIndex).posY);
                        System.out.println("Sprawdzany: " + weaponsArray.get(i).posX + " " + weaponsArray.get(i).posY + " " + distance);
                        if (distance > 0 && distance < targetDistance) {
                            targetDistance = distance;
                            targetX = weaponsArray.get(i).posX;
                            targetY = weaponsArray.get(i).posY;
                        }
                    }
                }
                System.out.println("Wybrana broń: " + targetX + " " + targetY + " " + targetDistance);
                //calling the method to change the coordinates of the NPC in targets direction
                for(int i = 1; i <= npcArray.get(npcIndex).stamina; i++) {
                    movement(targetX, targetY, npcArray.get(npcIndex).posX, npcArray.get(npcIndex).posY, npcIndex);
                    System.out.println(npcArray.get(npcIndex).posX + " " + npcArray.get(npcIndex).posY);
                    for(int j = 0; j < weaponsArray.size(); j++) {
                        System.out.println("Sprawdzany: " + weaponsArray.get(j).posX + " " + weaponsArray.get(j).posY);
                        if (npcArray.get(npcIndex).posX == weaponsArray.get(j).posX && npcArray.get(npcIndex).posY == weaponsArray.get(j).posY) {
                            weaponsArray.remove(j);
                            System.out.println(weaponsArray);
                            actionTaken = true;
                            break;
                        }
                    }
                    if(actionTaken) {
                        break;
                    }
                    for(int j = 0; j < heal.length; j++) {
                        System.out.println("Sprawdzany: " + heal[j][0] + " " + heal[j][1]);
                        if (npcArray.get(npcIndex).posX == heal[j][0] && npcArray.get(npcIndex).posY == heal[j][1]) {
                            heal = itemRemover(heal, j);
                            System.out.println(Arrays.deepToString(heal));
                            actionTaken = true;
                            break;
                        }
                    }
                    if(actionTaken) {
                        break;
                    }
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
    public static void movement(int targetX, int targetY, int x, int y, int npcIndex) {
        //TODO: zabezpieczyć przed wchodzeniem na NPC
        //targets coordinates are saved as targetX, targetY
        //proponuje, żeby NPC mogli się poruszać po skosie, bo wtedy ścieżki, po których się będą poruszać będą bardziej naturalne
        int moveX = npcArray.get(npcIndex).posX;
        int moveY = npcArray.get(npcIndex).posY;
        boolean isEmpty = true;
        System.out.println("Poruszam się");
        if(targetX > x) {
            moveX++;
        }
        else if(targetX < x) {
            moveX--;
        }
        if(targetY < y) {
            moveY--;
        }
        else if(targetY > y) {
            moveY++;
        }
        for(int i = 0; i < npcArray.size(); i++) {
            if(moveX == npcArray.get(npcIndex).posX || moveY == npcArray.get(npcIndex).posY) {
                isEmpty = false;
            }
        }
        if(isEmpty){
            npcArray.get(npcIndex).posX = moveX;
            npcArray.get(npcIndex).posY = moveY;
        }
        else{
            //Potencjalnie sprawdzimy czy ma więcej staminy, jak nie to musi go zaatakować, bo inaczej sam zostanie zaatakowany po następnym ruchu
            //Jeżeli jednak ma więcej staminy to powinien próbować uciec po przekątnej w stronę w którą się kierował
            //Bez znaczenia czy w góre po przekątnej czy w dół
            //TODO: co chcemy robić, gdy na drodze NPC stanie przeciwnik?
        }
    }
    public static void damageDealer(int DMG, int indexTarget){
        //zamiast koordynatów jest targetIndex jako index w tabeli/linijka w pliku
        System.out.println("Atakuje npc z indexem: " + indexTarget);
        npcArray.get(indexTarget).HP -= DMG;
        if (npcArray.get(indexTarget).HP <= 0) {npcArray.remove(indexTarget);}
        //TODO: Przerobić żeby działało z nową ArrayList NPC'ów
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
}
