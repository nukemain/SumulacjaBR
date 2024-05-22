import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Spawning {

    public static String[][] createMap(int sizeX, int sizeY){
        String[][] map = new String[sizeX][sizeY];
        //loop to fill the board with empty spaces ("[ ]")
        for(int y=0;y<sizeY;y++){
            for(int x=0;x<sizeX;x++){
                map[y][x] = "[ ]";
            }
        }
        return map;
    }

    public static String[][] updateMap(int sizeX, int sizeY,List<NPC> npcList,List<Weapon> weaponsList,List<int[]> medkitList){
        String[][] map = new String[sizeX][sizeY];
        //loop to fill the board with empty spaces ("[ ]")
        for(int y=0;y<sizeY;y++){
            for(int x=0;x<sizeX;x++){
                map[y][x] = "[ ]";
            }
        }
        for(int i=0;i<npcList.size();i++){
            map[npcList.get(i).posY][npcList.get(i).posX] = "[x]";
        }
        for(int i=0;i<weaponsList.size();i++){
            map[weaponsList.get(i).posY][weaponsList.get(i).posX] = "["+weaponsList.get(i).name+"]";
        }
        for(int i=0;i<medkitList.size();i++){
            map[medkitList.get(i)[1]][medkitList.get(i)[0]] = "[+]";
        }
        System.out.printf("refresh mapy");
        return map;
    }

    public static List<NPC> spawnNPCs(int sizeX, int sizeY, int NPCcount, String[][] board, List<NPC> npcArray){
        //Logic required for spawning NPC's
        //NOTE: this logic purposefully prevents spawns on the edges of the board
        Random rand = new Random();

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
                    spawnRandomNPC(NPC, posX, posY, npcArray);
                    NPC++;
                }else{
                    //however we sometimes ignore the check above (with a 15% rate) to make the NPC placement more random todo: 15 wybrane tak o, może jakaś lepsza wartość?
                    //This also prevents a situation where the amount of NPCs that we need to spawn would make it impossible
                    //to spawn them at least 1 tile away from each other.
                    if( (rand.nextInt(1,101)<=15) && (!Objects.equals(board[posY][posX], "[x]")) ){
                        board[posY][posX] = "[x]";
                        spawnRandomNPC(NPC, posX, posY, npcArray);
                        NPC++;
                    }
                }
            } catch(Exception e){/*po prostu zrespić ziutka gdzieś indziej*/}
        }
        return npcArray;
    }

    //Moja propozycja na spawny npc, potem można je przenieść do osobnych plików jak już będą się czymś wyróżniać np. Passive Regen
    public static List<NPC> spawnRandomNPC(int index, int posX, int posY, List<NPC> npcArray){
        int npcToSpawn = (int) (Math.random() * (5));
        switch (npcToSpawn){
            case 0:
                //Soldier
                npcArray.add(new NPC(index, posX, posY,150, 2, new Weapon("Knife", 15, 1,1, posX, posY)));
                break;
            case 1:
                //Medic
                npcArray.add(new NPC(index, posX, posY, 100, 2, new Weapon("Knife", 15, 1,1, posX, posY)));
                break;
            case 2:
                //Scout
                npcArray.add(new NPC(index, posX, posY, 90, 3, new Weapon("Knife", 15, 1,1, posX, posY)));
                break;
            case 3:
                //Sniper
                npcArray.add(new NPC(index, posX, posY, 100, 2, new Weapon("Knife", 15, 1,1, posX, posY)));
                break;
            case 4:
                //Spy
                npcArray.add(new NPC(index, posX, posY, 80, 2, new Weapon("Knife", 15, 1,1, posX, posY)));
                break;
        }
        return npcArray;
    }

    public static int checkNeighbours(String[][] map, int sizeX, int sizeY){
        //Loop below counts how many non NPC neighbouring tiles are on the board.
        //The found value is then used to decide the amount of weapons and medkits to spawn
        int NoNeighbours=0;
        for (int Y = 0; Y < sizeY; Y++) {
            for (int X = 0; X < sizeX; X++){
                try {
                    if (Objects.equals(map[Y][X], "[ ]") &&
                            Objects.equals(map[Y + 1][X], "[ ]") &&
                            Objects.equals(map[Y + 1][X + 1], "[ ]") &&
                            Objects.equals(map[Y][X + 1], "[ ]") &&
                            Objects.equals(map[Y - 1][X + 1], "[ ]") &&
                            Objects.equals(map[Y - 1][X], "[ ]") &&
                            Objects.equals(map[Y - 1][X - 1], "[ ]") &&
                            Objects.equals(map[Y][X - 1], "[ ]") &&
                            Objects.equals(map[Y + 1][X - 1], "[ ]")) {
                        NoNeighbours++;
                    }
                }catch (Exception e){}
            }
        }
        return NoNeighbours;
    }

    public static List<Weapon> spawnWeapons(String[][] map, int sizeX, int sizeY, int NPCcount, List<Weapon> weaponsArray){
        int WPNcount=0;
        int NoNeighbours=checkNeighbours(map, sizeX, sizeY);
        Random rand = new Random();
        //Logic deciding how many weapons should we spawn
        //numerical values chosen through trial and error
        if((NoNeighbours/5)<((int)(NPCcount*1.5))){ // check if we can spawn 1.5 weapon per NPC -> if not, use a smaller number
            WPNcount =(NoNeighbours/5);
        }else{
            WPNcount = ((int) (NPCcount * 1.5));
        }

        //Logic required for spawning Weapons
        //Works almost exactly the same way as the NPC spawning logic above
        int WPN=0;
        int weaponToSpawn = -1; //0-Knife, 1-Rifle, 2-Sniper rifle

        while(WPN<WPNcount){
            int posX= rand.nextInt(0, sizeX);
            int posY= rand.nextInt(0, sizeY);
            try {
                if (Objects.equals(map[posY][posX], "[ ]") &&
                        Objects.equals(map[posY + 1][posX], "[ ]") &&
                        Objects.equals(map[posY + 1][posX + 1], "[ ]") &&
                        Objects.equals(map[posY][posX + 1], "[ ]") &&
                        Objects.equals(map[posY - 1][posX + 1], "[ ]") &&
                        Objects.equals(map[posY - 1][posX], "[ ]") &&
                        Objects.equals(map[posY - 1][posX - 1], "[ ]") &&
                        Objects.equals(map[posY][posX - 1], "[ ]") &&
                        Objects.equals(map[posY + 1][posX - 1], "[ ]")) {
                    weaponToSpawn = (int) (Math.random() * (3));
                    switch (weaponToSpawn){
                        case 0:
                            weaponsArray.add(new Weapon("K", 15, 1,1, posX, posY));
                            map[posY][posX] = "[K]";
                            break;
                        case 1:
                            weaponsArray.add(new Weapon("R", 30, 2,2, posX, posY));
                            map[posY][posX] = "[R]";
                            break;
                        case 2:
                            weaponsArray.add(new Weapon("S", 50, 3,3, posX, posY));
                            map[posY][posX] = "[S]";
                            break;
                    }
                    WPN++;
                }
            } catch(Exception ignored){}
        }
        return weaponsArray;
    }
    public static List<int[]> spawnMedkits(String[][] map, int sizeX, int sizeY, int NPCcount, List<int[]> medpackArray){
        int MDPcount=0;
        int NoNeighbours=checkNeighbours(map, sizeX, sizeY);
        Random rand = new Random();
        if((NoNeighbours/5)<NPCcount){
            MDPcount =(NoNeighbours/5);
        }else{
            MDPcount = (NPCcount);
        }
        int MDP=0;
        while(MDP<MDPcount){
            int posX= rand.nextInt(0, sizeX);
            int posY= rand.nextInt(0, sizeY);
            try {
                if (Objects.equals(map[posY][posX], "[ ]") &&
                        Objects.equals(map[posY + 1][posX], "[ ]") &&
                        Objects.equals(map[posY + 1][posX + 1], "[ ]") &&
                        Objects.equals(map[posY][posX + 1], "[ ]") &&
                        Objects.equals(map[posY - 1][posX + 1], "[ ]") &&
                        Objects.equals(map[posY - 1][posX], "[ ]") &&
                        Objects.equals(map[posY - 1][posX - 1], "[ ]") &&
                        Objects.equals(map[posY][posX - 1], "[ ]") &&
                        Objects.equals(map[posY + 1][posX - 1], "[ ]")) {
                    map[posY][posX] = "[+]";
                    medpackArray.add(new int[]{posX, posY});
                    MDP++;
                }
            } catch(Exception ignored){}
        }
        return medpackArray;
    }
}