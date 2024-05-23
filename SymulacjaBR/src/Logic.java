import java.util.*;

import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

public class Logic {
    static List<NPC> npcList = new ArrayList<>();
    static List<Weapon> weaponsList = new ArrayList<>();
    static List<int[]> medkitList = new ArrayList<>();
    public static void main(String[] args)
    {
        //TODO: Na koniec jak już będzie śmigać dodać input od użytkownika w GUI.
        int NPCcount = 5;
        int sizeX=10;
        int sizeY=10;
        if(NPCcount>(sizeX-1)*(sizeY-1)*0.25){ //mniej niż 25% planszy to npc -> czemu 25%? liczba wybrana tak o z dupy
            System.out.println("Number of NPCs can not be higher than amount of fields.");
        }
        else{
            Symulacja(sizeX,sizeY,NPCcount);
        }
    }

    static void Symulacja(int sizeX,int sizeY,int NPCcount){

        String[][] map = Spawning.createMap(sizeX,sizeY);
        Spawning.spawnNPCs(sizeX,sizeY,NPCcount,map, npcList);//Logic required for spawning NPC's
        Spawning.spawnWeapons(map,sizeX,sizeY,NPCcount, weaponsList);//Spawning weapons on the map
        Spawning.spawnMedkits(map,sizeX,sizeY,NPCcount, medkitList);//Spawning medkits on the map

        //tymczasowa pętla do drukowania tablic
        while (npcList.size() > 1){
            for(int y=0;y<sizeY;y++){
                for(int x=0;x<sizeX;x++){
                    System.out.print(map[y][x]);
                }
                System.out.println();
            }
            for (int i=0;i<npcList.size();i++){
                decisionMaker(i);
            }
            map = Spawning.updateMap(sizeX,sizeY,npcList,weaponsList,medkitList);
            //to samo co system("pause")
            System.out.println("Press Enter to continue");
            try{System.in.read();}
            catch(Exception e){}
        }
        System.out.println("Winner is " + npcList.getFirst().index);
        /*
        System.out.println("---------------------------------");
        for (NPC npc : npcList) {
            System.out.println(npc);
        }
        System.out.println("---------------------------------");
        for (Weapon weapon : weaponsList) {
            System.out.println(weapon);
        }*/

    }
    public static void decisionMaker(int npcIndex) {
        //TODO: it should be using objects
        //Tablice do testu:
        /*int[][] npc = {{x, y, HP}, {7, 8, 100}, {3, 7, 54}};
        int[][] wpn = {{3, 3, 2}, {2, 3, 1}, {1, 9, 3}};
        int[][] heal = {{2, 2}, {6, 9}, {10, 10}};*/
        //Dane potrzebne do pracy programu:
        int targetX = -1; //współrzędna x celu
        int targetY = -1; //współrzędna y celu
        double targetDistance = 999; //odległość od celu
        int targetIndex = -1; //Numer celu do strzału w tabeli npc lub linijki w pliku potem
        boolean actionTaken = false;
        //sprawdzam czy HP mniejsze od 50% i jeśli tak to szuka najbliższej apteczki
        if((( (double) npcList.get(npcIndex).HP / npcList.get(npcIndex).maxHP) < 0.5) && medkitList.size() > 0) {
            //the loop finding the closest aid kit if HP under 50% and saving its coordinates
            for(int i = 0; i < medkitList.size(); i++) {
                double distance = distanceCalc(medkitList.get(i)[0],medkitList.get(i)[1], npcList.get(npcIndex).posX, npcList.get(npcIndex).posY);
                //System.out.println("Sprawdzany: " + heal[i][0] + " " + heal[i][1] + " " + distance); TEMP
                if(distance > 0 && distance < targetDistance) {
                    targetDistance = distance;
                    targetX = medkitList.get(i)[0];
                    targetY = medkitList.get(i)[1];
                    targetIndex = i;
                }
            }
            //System.out.println("Wybrana apteczka: " + targetX + " " + targetY + " " + targetDistance); TEMP
            //calling the method to move the target in direction of target
            for(int i = 1; i <= npcList.get(npcIndex).stamina; i++) {
                movement(targetX, targetY, npcList.get(npcIndex).posX, npcList.get(npcIndex).posY, npcIndex);
                //System.out.println(npcList.get(npcIndex).posX + " " + npcList.get(npcIndex).posY); TEMP
                if(npcList.get(npcIndex).posX == targetX && npcList.get(npcIndex).posY == targetY) {
                    //heal = itemRemover(heal, targetIndex);
                    medkitList.remove(targetIndex);
                    //System.out.println(Arrays.deepToString(heal)); TEMP
                    break;
                }
                else{
                    for(int j = 0; j < weaponsList.size(); j++) {
                        //System.out.println("Sprawdzany: " + weaponsList.get(j).posX + " " + weaponsList.get(j).posY); TEMP
                        if (npcList.get(npcIndex).posX == weaponsList.get(j).posX && npcList.get(npcIndex).posY == weaponsList.get(j).posY) {
                            weaponsList.remove(j);
                            //System.out.println(weaponsList); temp
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
            for(int i = 0; i < npcList.size(); i++) {
                double distance = distanceCalc(npcList.get(i).posX, npcList.get(i).posY, npcList.get(npcIndex).posX, npcList.get(npcIndex).posY);
                //System.out.println("Sprawdzany NPC: " + npcList.get(i).posX + " " + npcList.get(i).posY + " " + distance); TEMP
                if(distance > 0 && npcList.get(i).HP < targetHP && distance <= npcList.get(npcIndex).weapon.range) {
                    targetDistance = distance;
                    targetHP = npcList.get(i).HP;
                    targetX = npcList.get(i).posX;
                    targetY = npcList.get(i).posY;
                    targetIndex = i;
                    inRange = true;
                }
            }
            //System.out.println(targetX + " " + targetY + " " + targetDistance); temp
            //jeśli znalazło cel to wywołuje metodę odpowiedzialną za zadawanie obrażeń
            if(inRange) {
                damageDealer(npcList.get(npcIndex).weapon.damage, targetIndex);
            }
            //if there's no one in range it looks for the closest target of travel (another NPC or better weapon)
            else {
                //the loop finding the closest NPC and saving its coordinates
                for(int i = 0; i < npcList.size(); i++) {
                    double distance = distanceCalc(npcList.get(i).posX, npcList.get(i).posY, npcList.get(npcIndex).posX, npcList.get(npcIndex).posY);
                    //System.out.println("Sprawdzany: " + npcList.get(i).posX + " " + npcList.get(i).posY + " " + distance); temp
                    if(distance > 0 && distance < targetDistance) {
                        targetDistance = distance;
                        targetX = npcList.get(i).posX;
                        targetY = npcList.get(i).posY;
                    }
                }
                //this loop tries to find a weapon that is better than the one wielded by NPC and is closer than the closest enemy
                //if it finds such weapon it saves its coordinates
                //TODO: it could also be prevent from going through the loop if there's no weapon or no better weapon is present
                //System.out.println("Wybrany NPC: " + targetX + " " + targetY + " " + targetDistance); TEMP
                for(int i = 0; i < weaponsList.size(); i++) {
                    if(weaponsList.get(i).quality > npcList.get(npcIndex).weapon.quality) {
                        double distance = distanceCalc(weaponsList.get(i).posX, weaponsList.get(i).posY, weaponsList.get(npcIndex).posX, weaponsList.get(npcIndex).posY);
                        //System.out.println("Sprawdzany: " + weaponsList.get(i).posX + " " + weaponsList.get(i).posY + " " + distance); TEMP
                        if (distance > 0 && distance < targetDistance) {
                            targetDistance = distance;
                            targetX = weaponsList.get(i).posX;
                            targetY = weaponsList.get(i).posY;
                        }
                    }
                }
                //System.out.println("Wybrana broń: " + targetX + " " + targetY + " " + targetDistance); TEMP
                //calling the method to change the coordinates of the NPC in targets direction
                for(int i = 1; i <= npcList.get(npcIndex).stamina; i++) {
                    movement(targetX, targetY, npcList.get(npcIndex).posX, npcList.get(npcIndex).posY, npcIndex);
                    //System.out.println(npcList.get(npcIndex).posX + " " + npcList.get(npcIndex).posY); temp
                    for(int j = 0; j < weaponsList.size(); j++) {
                        //System.out.println("Sprawdzany: " + weaponsList.get(j).posX + " " + weaponsList.get(j).posY); TEMP
                        if (npcList.get(npcIndex).posX == weaponsList.get(j).posX && npcList.get(npcIndex).posY == weaponsList.get(j).posY) {
                            weaponsList.remove(j);
                            //System.out.println(weaponsList); temp
                            actionTaken = true;
                            break;
                        }
                    }
                    if(actionTaken) {
                        break;
                    }
                    for(int j = 0; j < medkitList.size(); j++) {
                        //System.out.println("Sprawdzany: " + heal[j][0] + " " + heal[j][1]); TEMP
                        if (npcList.get(npcIndex).posX == medkitList.get(j)[0] && npcList.get(npcIndex).posY == medkitList.get(j)[1]) {
                            //heal = itemRemover(heal, j);
                            medkitList.remove(j);
                            //System.out.println(Arrays.deepToString(heal));
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
        int moveX = npcList.get(npcIndex).posX;
        int moveY = npcList.get(npcIndex).posY;
        boolean isEmpty = true;
        System.out.println(npcList.get(npcIndex).index + " porusza się z "+ npcList.get(npcIndex).posX+" "+npcList.get(npcIndex).posY);
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
        for(int i = 0; i < npcList.size(); i++) {
            if(moveX == npcList.get(i).posX && moveY == npcList.get(i).posY) {
                isEmpty = false;
            }
        }
        if(isEmpty){
            npcList.get(npcIndex).posX = moveX;
            npcList.get(npcIndex).posY = moveY;
            System.out.println("na "+ npcList.get(npcIndex).posX+" "+npcList.get(npcIndex).posY);
        }
        else{
            System.out.println("Space occupied");
            //Potencjalnie sprawdzimy czy ma więcej staminy, jak nie to musi go zaatakować, bo inaczej sam zostanie zaatakowany po następnym ruchu
            //Jeżeli jednak ma więcej staminy to powinien próbować uciec po przekątnej w stronę w którą się kierował
            //Bez znaczenia czy w góre po przekątnej czy w dół
            //TODO: co chcemy robić, gdy na drodze NPC stanie przeciwnik? odp: walczyć i tyle imo
        }
    }
    public static void damageDealer(int DMG, int indexTarget){
        //zamiast koordynatów jest targetIndex jako index w tabeli/linijka w pliku
        System.out.println("Atakuje npc z indexem: " + indexTarget);
        npcList.get(indexTarget).HP -= DMG;
        if (npcList.get(indexTarget).HP <= 0) {
            npcList.remove(indexTarget);
            System.out.println(indexTarget + " was killed");
        }
        //TODO: Przerobić żeby działało z nową ArrayList NPC'ów
        //TODO: remove npc from array with index
    }
    /*
    public static int[][] itemRemover(int[][] item, int indexTarget){
        //TODO: czy to jest do czegokolwiek potrzebne?
        //TODO: (cd.) jeśli robimy wszystko na Listach (weaponList , npcList , medkitList) nie wystarczy samo ___Array.remove()?
        //TODO: Już nie, było potrzebne, gdy pracowaliśmy na tablicach
        int[][] itemRemover = new int[item.length - 1][];
        for (int i = 0, k = 0; i < item.length; i++) {
            if (i != indexTarget) {
                itemRemover[k] = item[i];
                k++;
            }
        }
        //System.out.println(Arrays.deepToString(itemRemover)); TEMP
        return itemRemover;
    }
    */
}
