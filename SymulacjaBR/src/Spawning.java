import NPCClasses.*;
import WeaponClasses.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Class responsible for placing NPC's, weapons, and medkits on the map
 */
public class Spawning {

    /**
     * Method used to create an empty map. First used in the console version of the program, the text implementation of the map stayed in our code as we were planning to write simulation's state after each turn to Data_collected.csv. This idea however did not come to fruition.<br>
     * The variable returned here is also used in spawning all elements of the simulation.
     * @param size size of the simulation's board
     * @return returns a List of Lists of Strings representing the simulation's empty map
     */
    public static List<List<String>> createMap(int size){
        List<List<String>> map = new ArrayList<>();
        //loop to fill the board with empty spaces ("[ ]")
        for(int y=0;y<size;y++){
            map.add(new ArrayList<>());
            for(int x=0;x<size;x++){
                map.get(y).add("[ ]");
            }
        }
        return map;
    }

    /**
     * Method used to update the map with current positions of each element inside the simulation.
     * @param medkitList List of medkits  
     * @param npcList List of npc's
     * @param weaponsList list of weapon's
     * @param size size of the simulation's board
     * @return returns a List of Lists of Strings representing the simulation's empty map
     */
    public static List<List<String>> updateMap(int size, List<NPC> npcList, List<Weapon> weaponsList, List<int[]> medkitList){
        List<List<String>> map = new ArrayList<>();
        //loop to fill the board with empty spaces ("[ ]")
        for(int y=0;y<size;y++){
            map.add(new ArrayList<>());
            for(int x=0;x<size;x++){
                map.get(y).add("[ ]");
            }
        }
        for(int i=0;i<npcList.size();i++){
            map.get(npcList.get(i).posY).set(npcList.get(i).posX, "[" + npcList.get(i).symbol + "]");
        }
        for(int i=0;i<weaponsList.size();i++){
            map.get(weaponsList.get(i).posY).set(weaponsList.get(i).posX, "[" + weaponsList.get(i).name.charAt(0) + "]");
        }
        for(int i=0;i<medkitList.size();i++){
            map.get(medkitList.get(i)[1]).set(medkitList.get(i)[0], "[+]");
        }
        return map;
    }

    /**
     * Method used to choose the coordinates to spawn NPCs<br>
     * NOTE: this method purposefully prevents spawns on the edges of the board
     * @param size size of simulation's board
     * @param NPCcount amount of NPC's to be spawned
     * @param board variable containing the string representation of the board
     * @param npcArray list of NPC's inside the simulation
     * @return List of newly created NPC objects to be used inside the simulation
     */
    public static List<NPC> spawnNPCs(int size, int NPCcount, List<List<String>> board, List<NPC> npcArray){
        Random rand = new Random();

        int NPC=0; //counter for how many NPCClasses.NPC have been spawned
        while(NPC<NPCcount){
            int posX= rand.nextInt(0, size);
            int posY= rand.nextInt(0, size);
            try {      //check if the random coordinates are not occupied and don't neighbour with any other NPCClasses.NPC
                if (Objects.equals(board.get(posY).get(posX), "[ ]") &&
                        Objects.equals(board.get(posY + 1).get(posX), "[ ]") &&
                        Objects.equals(board.get(posY + 1).get(posX + 1), "[ ]") &&
                        Objects.equals(board.get(posY).get(posX + 1), "[ ]") &&
                        Objects.equals(board.get(posY - 1).get(posX + 1), "[ ]") &&
                        Objects.equals(board.get(posY - 1).get(posX), "[ ]") &&
                        Objects.equals(board.get(posY - 1).get(posX - 1), "[ ]") &&
                        Objects.equals(board.get(posY).get(posX - 1), "[ ]") &&
                        Objects.equals(board.get(posY + 1).get(posX - 1), "[ ]")) {
                    board.get(posY).set(posX, "[x]");
                    spawnRandomNPC(NPC, posX, posY, npcArray);
                    NPC++;
                }else{
                    //however we sometimes ignore the check above (with a 15% rate) to make the NPCClasses.NPC placement more random
                    //This also prevents a situation where the amount of NPCs that we need to spawn would make it impossible
                    //to spawn them at least 1 tile away from each other.
                    if( (rand.nextInt(1,101)<=15) && (!Objects.equals(board.get(posY).get(posX), "[x]")) ){
                        board.get(posY).set(posX, "[x]");
                        spawnRandomNPC(NPC, posX, posY, npcArray);
                        NPC++;
                    }
                }
            } catch(Exception e){}
        }
        return npcArray;
    }

    /**
     * Method used to create the objects of NPC subclasses.
     * @param index index of the spawned NPC
     * @param posX X position of the spawned NPC
     * @param posY Y position of the spawned NPC
     * @param npcArray List of NPC's which the NPC will be added to.
     * @return updated List of NPC's containing the new NPC.
     */
    public static List<NPC> spawnRandomNPC(int index, int posX, int posY, List<NPC> npcArray){
        int npcToSpawn = (int) (Math.random() * (5));
        switch (npcToSpawn){
            case 0:
                //Soldier
                npcArray.add(new Soldier(index, posX, posY, new Knife(posX, posY)));
                break;
            case 1:
                //Medic
                npcArray.add(new Medic(index, posX, posY,  new Knife(posX, posY)));
                break;
            case 2:
                //Scout
                npcArray.add(new Scout(index, posX, posY, new Knife(posX, posY)));
                break;
            case 3:
                //Sniper
                npcArray.add(new Sniper(index, posX, posY,  new Knife(posX, posY)));
                break;
            case 4:
                //Spy
                npcArray.add(new Spy(index, posX, posY,  new Knife(posX, posY)));
                break;
        }
        return npcArray;
    }

    /**
     * Method counts how many tiles on the map do not neighbour any NPC
     * @param map string representation of the simulation's map
     * @param size size of simulation's board
     * @return returns the amount of tiles on the map with no neighbours (including diagonally)
     */
    private static int checkNeighbours(List<List<String>> map, int size){
        //Loop below counts how many non NPCClasses.NPC neighbouring tiles are on the board.
        //The found value is then used to decide the amount of weapons and medkits to spawn
        int NoNeighbours=0;
        for (int Y = 0; Y < size; Y++) {
            for (int X = 0; X < size; X++){
                try {
                    if (Objects.equals(map.get(Y).get(X), "[ ]") &&
                            Objects.equals(map.get(Y + 1).get(X), "[ ]") &&
                            Objects.equals(map.get(Y + 1).get(X + 1), "[ ]") &&
                            Objects.equals(map.get(Y).get(X + 1), "[ ]") &&
                            Objects.equals(map.get(Y - 1).get(X + 1), "[ ]") &&
                            Objects.equals(map.get(Y - 1).get(X), "[ ]") &&
                            Objects.equals(map.get(Y - 1).get(X - 1), "[ ]") &&
                            Objects.equals(map.get(Y).get(X - 1), "[ ]") &&
                            Objects.equals(map.get(Y + 1).get(X - 1), "[ ]")) {
                        NoNeighbours++;
                    }
                }catch (Exception e){}
            }
        }
        return NoNeighbours;
    }

    /**
     * Method used to choose the coordinates for weapons to spawn and then create the objects of Weapons subclasses
     * @param map string representation of the simulation's map
     * @param size size of simulation's board
     * @param NPCcount amount of NPC's present in the simulation
     * @param weaponsArray List of existing weapon objects
     * @return updated List containing the newly spawned weapon
     */
    public static List<Weapon> spawnWeapons(List<List<String>> map, int size, int NPCcount, List<Weapon> weaponsArray){
        int WPNcount=0;
        int NoNeighbours=checkNeighbours(map, size);
        Random rand = new Random();
        //Logic deciding how many weapons should we spawn
        //numerical values chosen through trial and error
        if((NoNeighbours/5)<((int)(NPCcount*1.5))){ // check if we can spawn 1.5 weapon per NPCClasses.NPC -> if not, use a smaller number
            WPNcount =(NoNeighbours/5);
        }else{
            WPNcount = ((int) (NPCcount * 1.5));
        }

        //Logic required for spawning Weapons
        //Works almost exactly the same way as the NPCClasses.NPC spawning logic above
        int WPN=0;
        int weaponToSpawn = -1; //0-Knife, 1-Rifle, 2-NPCClasses.Sniper rifle

        while(WPN<WPNcount){
            int posX= rand.nextInt(0, size);
            int posY= rand.nextInt(0, size);
            try {
                if (Objects.equals(map.get(posY).get(posX), "[ ]") &&
                        Objects.equals(map.get(posY + 1).get(posX), "[ ]") &&
                        Objects.equals(map.get(posY + 1).get(posX + 1), "[ ]") &&
                        Objects.equals(map.get(posY).get(posX + 1), "[ ]") &&
                        Objects.equals(map.get(posY - 1).get(posX + 1), "[ ]") &&
                        Objects.equals(map.get(posY - 1).get(posX), "[ ]") &&
                        Objects.equals(map.get(posY - 1).get(posX - 1), "[ ]") &&
                        Objects.equals(map.get(posY).get(posX - 1), "[ ]") &&
                        Objects.equals(map.get(posY + 1).get(posX - 1), "[ ]")) {
                    weaponToSpawn = (int) (Math.random() * (100));
                    if(weaponToSpawn < 40){
                        weaponsArray.add(new Handgun(posX, posY));
                        map.get(posY).set(posX, "[H]");
                    }
                    else if(weaponToSpawn >= 40 && weaponToSpawn <70){
                        weaponsArray.add(new Shotgun(posX, posY));
                        map.get(posY).set(posX, "[B]");
                    }
                    else if(weaponToSpawn >= 70 && weaponToSpawn <80){
                        weaponsArray.add(new Rifle(posX, posY));
                        map.get(posY).set(posX, "[R]");
                    }
                    else if(weaponToSpawn >= 80 && weaponToSpawn <90){
                        weaponsArray.add(new SMG(posX, posY));
                        map.get(posY).set(posX, "[U]");
                    }
                    else if(weaponToSpawn >= 90){
                        weaponsArray.add(new SniperRifle(posX, posY));
                        map.get(posY).set(posX, "[S]");
                    }
                    WPN++;
                }
            } catch(Exception ignored){}
        }
        return weaponsArray;
    }

    /**
     * Method used to spawn medkits in the simulation
     * @param map string representation of the simulation's map
     * @param size size of simulation's board
     * @param NPCcount amount of NPC's present in the simulation
     * @param medpackList List of existing medkits
     * @return updated List containing the newly spawned medkit
     */
    public static List<int[]> spawnMedkits(List<List<String>> map, int size, int NPCcount, List<int[]> medpackList){
        int MDPcount=0;
        int NoNeighbours=checkNeighbours(map, size);
        Random rand = new Random();
        if((NoNeighbours/5)<NPCcount){
            MDPcount =(NoNeighbours/5);
        }else{
            MDPcount = (NPCcount);
        }
        int MDP=0;
        while(MDP<MDPcount){
            int posX= rand.nextInt(0, size);
            int posY= rand.nextInt(0, size);
            try {
                if (Objects.equals(map.get(posY).get(posX), "[ ]") &&
                        Objects.equals(map.get(posY + 1).get(posX), "[ ]") &&
                        Objects.equals(map.get(posY + 1).get(posX + 1), "[ ]") &&
                        Objects.equals(map.get(posY).get(posX + 1), "[ ]") &&
                        Objects.equals(map.get(posY - 1).get(posX + 1), "[ ]") &&
                        Objects.equals(map.get(posY - 1).get(posX), "[ ]") &&
                        Objects.equals(map.get(posY - 1).get(posX - 1), "[ ]") &&
                        Objects.equals(map.get(posY).get(posX - 1), "[ ]") &&
                        Objects.equals(map.get(posY + 1).get(posX - 1), "[ ]")) {
                    map.get(posY).set(posX, "[+]");
                    medpackList.add(new int[]{posX, posY});
                    MDP++;
                }
            } catch(Exception ignored){}
        }
        return medpackList;
    }
}