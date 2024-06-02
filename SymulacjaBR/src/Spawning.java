import NPCClasses.*;
import WeaponClasses.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Spawning {

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

    public static List<List<String>> updateMap(int size, List<NPC> npcList, List<Weapon> weaponsList, List<int[]> medkitList){
        List<List<String>> map = new ArrayList<>();
        //loop to fill the board with empty spaces ("[ ]")
        for(int y=0;y<Controller.size;y++){
            map.add(new ArrayList<>());
            for(int x=0;x<Controller.size;x++){
                map.get(y).add("[ ]");
            }
        }
        for(int i=0;i<npcList.size();i++){
            map.get(npcList.get(i).posY).set(npcList.get(i).posX, "[" + npcList.get(i).symbol + "]"); //symbol NPC'ta w przyszłości będzie to ikonka w gui
        }
        for(int i=0;i<weaponsList.size();i++){
            map.get(weaponsList.get(i).posY).set(weaponsList.get(i).posX, "[" + weaponsList.get(i).name.charAt(0) + "]"); //pierwszy znak z nzwy itemu
        }
        for(int i=0;i<medkitList.size();i++){
            map.get(medkitList.get(i)[1]).set(medkitList.get(i)[0], "[+]");
        }
        return map;
    }

    public static List<NPC> spawnNPCs(int size, int NPCcount, List<List<String>> board, List<NPC> npcArray){
        //Logic required for spawning NPCClasses.NPC's
        //NOTE: this logic purposefully prevents spawns on the edges of the board
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
            } catch(Exception e){/*po prostu zrespić ziutka gdzieś indziej*/}
        }
        return npcArray;
    }

    //Moja propozycja na spawny npc, potem można je przenieść do osobnych plików jak już będą się czymś wyróżniać np. Passive Regen
    public static List<NPC> spawnRandomNPC(int index, int posX, int posY, List<NPC> npcArray){
        int npcToSpawn = (int) (Math.random() * (5));
        //int npcToSpawn = 0;
        switch (npcToSpawn){
            case 0:
                //Soldier
                npcArray.add(new Soldier(index, posX, posY,150, 2, new Knife("Knife", 15, 1,0, posX, posY), "Σ"));
                break;
            case 1:
                //Medic
                npcArray.add(new Medic(index, posX, posY, 100, 2, new Knife("Knife", 15, 1,0, posX, posY), "μ"));
                break;
            case 2:
                //Scout
                npcArray.add(new Scout(index, posX, posY, 90, 3, new Knife("Knife", 15, 1,0, posX, posY), "Λ"));
                break;
            case 3:
                //Sniper
                npcArray.add(new Sniper(index, posX, posY, 100, 2, new Knife("Knife", 15, 1,0, posX, posY), "Θ"));
                break;
            case 4:
                //Spy
                npcArray.add(new Spy(index, posX, posY, 80, 2, new Knife("Knife", 15, 1,0, posX, posY), "Ω"));
                break;
        }
        return npcArray;
    }

    public static int checkNeighbours(List<List<String>> map, int size){
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
                    weaponToSpawn = (int) (Math.random() * (5));
                    switch (weaponToSpawn){
                        case 0:
                            weaponsArray.add(new Handgun("Handgun", 25, 1,1, posX, posY));
                            map.get(posY).set(posX, "[H]");
                            break;
                        case 1:
                            weaponsArray.add(new Rifle("Rifle", 35, 2,2, posX, posY));
                            map.get(posY).set(posX, "[R]");
                            break;
                        case 2:
                            weaponsArray.add(new SMG("SMG", 50, 1,2, posX, posY));
                            map.get(posY).set(posX, "[U]");
                            break;
                        case 3:
                            weaponsArray.add(new Shotgun("Shotgun", 50, 1,2, posX, posY));
                            map.get(posY).set(posX, "[B]");
                            break;
                        case 4:
                            weaponsArray.add(new SniperRifle("SniperRifle", 40, 3,3, posX, posY));
                            map.get(posY).set(posX, "[S]");
                            break;
                    }
                    WPN++;
                }
            } catch(Exception ignored){}
        }
        return weaponsArray;
    }
    public static List<int[]> spawnMedkits(List<List<String>> map, int size, int NPCcount, List<int[]> medpackArray){
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
                    medpackArray.add(new int[]{posX, posY});
                    MDP++;
                }
            } catch(Exception ignored){}
        }
        return medpackArray;
    }
}