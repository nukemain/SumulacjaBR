import java.util.*;

import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

public class Logic {
    public static void main(String[] args)
    {
        Symulacja(30,30,15); //tymczasowo zhardkodowane
        //TODO: wyłapywanie czy NPCcount nie jest większy od maksymalnej możliwej ilości npc (liczb pól w planszy) - zrobić to przed odpaleniem symulacji
        decisionMaker();
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

        String[][]  board = new String[sizeX][sizeY];
        Random rand = new Random();

        //loop to fill the board with empty spaces ("[ ]")
        for(int y=0;y<sizeY;y++){
            for(int x=0;x<sizeX;x++){
                board[y][x] = "[ ]";
            }
        }

        //TODO: limit npc (prawdopodobnie (sizeX-1 * sizeY-1)-1) - kod za to odpowiedzialny dać przed wywołaniem symulacji !!!
        //Logic required for spawning NPC's
        //NOTE: this logic purposefully prevents spawns on the edges of the board
        int NPC=0; //counter for how many NPC have been spawned
        while(NPC<NPCcount){
            int posX= rand.nextInt(0, sizeX);
            int posY= rand.nextInt(0, sizeY);
            try {      //check if the random coordinates are not occupied and don't neighbour with any other NPC
                if (!Objects.equals(board[posY][posX], "[x]") &&
                        !Objects.equals(board[posY + 1][posX], "[x]") &&
                        !Objects.equals(board[posY + 1][posX + 1], "[x]") &&
                        !Objects.equals(board[posY][posX + 1], "[x]") &&
                        !Objects.equals(board[posY - 1][posX + 1], "[x]") &&
                        !Objects.equals(board[posY - 1][posX], "[x]") &&
                        !Objects.equals(board[posY - 1][posX - 1], "[x]") &&
                        !Objects.equals(board[posY][posX - 1], "[x]") &&
                        !Objects.equals(board[posY + 1][posX - 1], "[x]")) {
                    board[posY][posX] = "[x]";
                    //todo: tu wywołanie konstruktora klasy NPC
                    NPC++;
                }else{
                    //however we sometimes ignore the check above (with a 15% rate) to make the NPC placement more random todo: 15 wybrane tak o, może jakaś lepsza wartość?
                    //This also prevents a situation where the amount of NPCs that we need to spawn would make it impossible
                    //to spawn them at least 1 tile away from each other.
                    if( (rand.nextInt(1,101)<=15) && (!Objects.equals(board[posY][posX], "[x]")) ){
                        board[posY][posX] = "[x]";
                        //todo: tu wywołanie konstruktora klasy NPC
                        NPC++;
                    }
                }
            } catch(Exception e){/*po prostu zrespić ziutka`gdzieś indziej*/}
        }

        //Loop below counts how many non NPC neighbouring tiles are on the board.
        //The found value is then used to decide the amount of weapons and medkits to spawn
        int NoNeighbours=0;
        for (int Y = 0; Y < sizeY; Y++) {
            for (int X = 0; X < sizeX; X++){
                try {
                    if (Objects.equals(board[Y][X], "[ ]") &&
                            Objects.equals(board[Y + 1][X], "[ ]") &&
                            Objects.equals(board[Y + 1][X + 1], "[ ]") &&
                            Objects.equals(board[Y][X + 1], "[ ]") &&
                            Objects.equals(board[Y - 1][X + 1], "[ ]") &&
                            Objects.equals(board[Y - 1][X], "[ ]") &&
                            Objects.equals(board[Y - 1][X - 1], "[ ]") &&
                            Objects.equals(board[Y][X - 1], "[ ]") &&
                            Objects.equals(board[Y + 1][X - 1], "[ ]")) {
                        NoNeighbours++;
                    }
                }catch (Exception e){}
            }
        }

        int WPNcount=0;

        //Logic deciding how many weapons should we spawn
        //numerical values chosen through trial and error
        if((NoNeighbours/5)<((int)(NPC*1.5))){ // check if we can spawn 1.5 weapon per NPC -> if not, use a smaller number
            WPNcount =(NoNeighbours/5);
        }else{
            WPNcount = ((int) (NPC * 1.5));
        }

        //Logic required for spawning Weapons
        //Works almost exactly the same way as the NPC spawning logic above
        int WPN=0;
        int weaponToSpawn = -1; //0-Knife, 1-Rifle, 2-Sniper rifle
        List<Weapon> weapons = new ArrayList<>();
        while(WPN<WPNcount){
            int posX= rand.nextInt(0, sizeX);
            int posY= rand.nextInt(0, sizeY);
            try {
                if (Objects.equals(board[posY][posX], "[ ]") &&
                        Objects.equals(board[posY + 1][posX], "[ ]") &&
                        Objects.equals(board[posY + 1][posX + 1], "[ ]") &&
                        Objects.equals(board[posY][posX + 1], "[ ]") &&
                        Objects.equals(board[posY - 1][posX + 1], "[ ]") &&
                        Objects.equals(board[posY - 1][posX], "[ ]") &&
                        Objects.equals(board[posY - 1][posX - 1], "[ ]") &&
                        Objects.equals(board[posY][posX - 1], "[ ]") &&
                        Objects.equals(board[posY + 1][posX - 1], "[ ]")) {
                    weaponToSpawn = (int) (Math.random() * (3));
                    switch (weaponToSpawn){
                        case 0:
                            weapons.add(new Weapon("Knife", 15, 1,1, posX, posY));
                            board[posY][posX] = "[K]";
                            break;
                        case 1:
                            weapons.add(new Weapon("Rifle", 30, 2,2, posX, posY));
                            board[posY][posX] = "[R]";
                            break;
                        case 2:
                            weapons.add(new Weapon("SniperRifle", 50, 3,3, posX, posY));
                            board[posY][posX] = "[S]";
                            break;
                    }
                    WPN++;
                }
            } catch(Exception ignored){}
        }


        //tymcasowa pętla do drukowania tablicy
        for(int y=0;y<sizeY;y++){
            for(int x=0;x<sizeX;x++){
                System.out.print(board[y][x]);
            }
            System.out.println();
        }
    }
    public static void decisionMaker() {
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
        //TODO: zabezpieczyć przed wchodzeniem na NPC
        //targets coordinates are saved as targetX, targetY
        //proponuje, żeby NPC mogli się poruszać po skosie, bo wtedy ścieżki, po których się będą poruszać będą bardziej naturalne
        int moveX = npc[npcIndex][0];
        int moveY = npc[npcIndex][1];
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
        for(int i = 0; i < npc.length; i++) {
            if(moveX == npc[i][0] || moveY == npc[i][1]) {
                isEmpty = false;
            }
        }
        if(isEmpty){
            npc[npcIndex][0] = moveX;
            npc[npcIndex][1] = moveY;
        }
        else{
            //TODO: co chcemy robić, gdy na drodze NPC stanie przeciwnik?
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
}
