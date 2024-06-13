import NPCClasses.*;
import javax.swing.*;
import WeaponClasses.Weapon;

import java.io.IOException;
import java.util.*;
import java.util.List;

import static java.lang.Math.abs;
import static java.lang.Math.sqrt;


/**
 * Class responsible for all logic used inside the Simulation.
 */
public class Logic {

    /**
     * Size of the simulation's map
     */
    static int size = 1;
    /**
     * Amount of NPCs on the map
     */
    static int NPCcount = 10;
    /**
     * Main JFrame object to which GUI is bound to
     */
    static JFrame SimulationFrame = new JFrame();
    //Lists required for logic to function


    /**
     * List of all NPC objects present in the simulation
     */
    static List<NPC> npcList = new ArrayList<>();
    /**
     * List of all weapon objects present in the simulation
     */
    static List<Weapon> weaponsList = new ArrayList<>();
    /**
     * List of all medkits present in the simulation
     */
    static List<int[]> medkitList = new ArrayList<>();

    /**
     * Number of rounds in current simulation
     */
    public static int roundsCounter = 0;


    /**
     * Boolean needed to pause main simulation's loop when a button is pressed
     */
    //required for pausing
    static boolean buttonPressed = false;
    /**
     * Boolean needed to pause main simulation's loop when a button is pressed and held
     */
    static boolean buttonHeld = false;
    /**
     * Object needed to work as a lock pausing the execution of simulation's loop
     */
    static final Object lock = new Object();
    /**
     * String representation of the two-dimensional simulation's map
     */
    static List<List<String>> map = new ArrayList<>(); //list used to spawn NPCs, weapon and medkits


    /**
     * Main function of the program, is used only to call Simulation()
     */
    //method used to begin the simulation
    public static void main(String[] args) throws IOException {
        Simulation();
    }

    /**
     * Function containing the main loop of the simulation, called only from main()
     */
    static void Simulation() throws IOException {
        CSVGenerator csvObject = new CSVGenerator();
        GUI.SimulationGUI(Logic.SimulationFrame);
        Logic.SimulationFrame.setVisible(true);

        synchronized (lock) {
            while (!buttonPressed) {
                try {
                    lock.wait();
                } catch (InterruptedException ignored) {}
            }
            buttonPressed = false;
        }

        GUI.refreshTerrain();

        while (npcList.size() > 1){
            csvObject.dataAdder(npcList.size(), weaponsList.size(), medkitList.size());

            //================================================
            //locking the loop's execution until the "Następna tura" buttonTop is pressed
            synchronized (lock) {
                while (!buttonPressed) {
                    try {
                        lock.wait();
                    } catch (InterruptedException ignored) {}
                }
                buttonPressed = false;
            }
            //================================================
            GUI.display.append("================================================================================================================================================================================================\n");
            GUI.display.append("[Tura nr"+ roundsCounter +"]\n");

            // do "logic" for each npc in npcList
            for (int i=0;i<npcList.size();i++){
                decisionMaker(i);
            }
            Spawning.updateMap(Logic.size,npcList,weaponsList,medkitList);
            GUI.refreshGUIMap();
            //the part of code used to create a shrinking safe zone
            if(roundsCounter%2==0){
            TerrainGenerator.ShrinkZone(Logic.size,Logic.size/2,Logic.size/2,roundsCounter);
            GUI.refreshTerrain();
            }

        }
        Spawning.updateMap(Logic.size,npcList,weaponsList,medkitList);
        GUI.refreshGUIMap();
        csvObject.dataAdder(npcList.size(), weaponsList.size(), medkitList.size());
        csvObject.csvWriter();
        GUI.SimulationGUIEnd();
    }


    /**
     * Method used for NPCs to make decisions and implement them
     * @param npcIndex index of NPC from npcList for which we do logic decisions
     */
    public static void decisionMaker(int npcIndex) {
        int targetX = -1; //x coordinate of NPC's target
        int targetY = -1; //y coordinate of NPC's target
        double targetDistance = 999; //distance from NPC to the target
        int targetIndex = -1; //index of the target in the given list
        boolean actionTaken = false; //boolean checking if the NPC already has taken its action and should finish his round
        //number used to distinguish the type of terrain on which the NPC is standing
        int currentTerrain = TerrainGenerator.terrainMap.get(npcList.get(npcIndex).posY).get(npcList.get(npcIndex).posX);
        //current range of the NPC (it could change based on the currentTerrain)
        double currentRange = npcList.get(npcIndex).weapon.range;
        //current stamina of the NPC (it could change based on the currentTerrain)
        int currentStamina = npcList.get(npcIndex).stamina;
        //removing medkits from the medkitsList if they are out of the safe zone
        for(int i=0; i<medkitList.size();i++){
            if(TerrainGenerator.terrainMap.get(medkitList.get(i)[0]).get(medkitList.get(i)[1])==4){
                medkitList.remove(i);
            }
        }
        //NPC uses its ability every turn
        if(Objects.equals(npcList.get(npcIndex).symbol, "M") || Objects.equals(npcList.get(npcIndex).symbol, "S")){
            npcList.get(npcIndex).Ability();
        }
        //applying effects on NPC based on its currentTerrain
        switch(currentTerrain) {
            case 0://desert
                if(currentStamina > 1) {
                    currentStamina -= 1;
                }
                break;
            case 2://forest
                currentRange = sqrt(2);
                break;
            case 3://mountains
                if(!npcList.get(npcIndex).weapon.name.equals("Knife")) {
                    currentRange += 1;
                }
                break;
            case 4: //zone
                npcList.get(npcIndex).HP -=10;
                if(npcList.get(npcIndex).HP<=0){
                    GUI.display.append(npcList.get(npcIndex).name+" umarł od strefy!\n");
                    npcList.remove(npcIndex);
                    return;
                }
                //if the NPC is out of the safe zone it finds the shortest way to get into the safe zone
                for(int y=0;y<Logic.size;y++){
                    for(int x=0;x<Logic.size;x++){
                        if(TerrainGenerator.terrainMap.get(y).get(x)!=4){
                            double distance = distanceCalc(y,x,npcList.get(npcIndex).posX, npcList.get(npcIndex).posY);
                            if(distance<targetDistance){
                                targetDistance=distance;
                                targetX = x;
                                targetY = y;
                            }
                        }
                    }
                }
                //calling the method to move the target in direction of target
                for(int i = 1; i <= currentStamina; i++) {
                    movement(targetX, targetY, npcList.get(npcIndex).posX, npcList.get(npcIndex).posY, npcIndex); //ucieczka ze strefy zawsze priorytetem
                }
                break;
        }

        //if HP is less than 50% of the maximum HP it finds the closest medkit and sets it as a target
        if((( (double) npcList.get(npcIndex).HP / npcList.get(npcIndex).maxHP) < 0.5) && !medkitList.isEmpty()) {
            //the loop finding the closest aid kit if HP under 50% and saving its coordinates
            for(int i = 0; i < medkitList.size(); i++) {
                if(TerrainGenerator.terrainMap.get(medkitList.get(i)[0]).get(medkitList.get(i)[1])==4){
                    break; //if medkit is in the zone, don't go for it
                }
                double distance = distanceCalc(medkitList.get(i)[0],medkitList.get(i)[1], npcList.get(npcIndex).posX, npcList.get(npcIndex).posY);
                if(distance > 0 && distance < targetDistance) {
                    targetDistance = distance;
                    targetX = medkitList.get(i)[0];
                    targetY = medkitList.get(i)[1];
                    targetIndex = i;
                }
            }
            //calling the method to move the target in direction of target
            for(int i = 1; i <= currentStamina; i++) {
                movement(targetX, targetY, npcList.get(npcIndex).posX, npcList.get(npcIndex).posY, npcIndex);
                if(npcList.get(npcIndex).posX == targetX && npcList.get(npcIndex).posY == targetY) {//if on target...
                    for(int j = 0; j < medkitList.size(); j++) {//...pick up the medkit
                        if (npcList.get(npcIndex).posX == medkitList.get(j)[0] && npcList.get(npcIndex).posY == medkitList.get(j)[1]) {
                            String text = "NPC "+npcList.get(npcIndex).name+" podniósł apteczkę! HP "+npcList.get(npcIndex).HP+"->";
                            if((npcList.get(npcIndex).HP += 30)>npcList.get(npcIndex).maxHP){
                                npcList.get(npcIndex).HP=npcList.get(npcIndex).maxHP;
                            }else{
                                npcList.get(npcIndex).HP += 30;
                            }
                            text = text + npcList.get(npcIndex).HP;
                            GUI.display.append(text+"\n");
                            medkitList.remove(j);
                            actionTaken = true;
                            break;
                        }
                    }
                    break;
                }
                else{
                    for(int j = 0; j < weaponsList.size(); j++) {
                        if (npcList.get(npcIndex).posX == weaponsList.get(j).posX && npcList.get(npcIndex).posY == weaponsList.get(j).posY) {
                            npcList.get(npcIndex).weapon = weaponsList.get(j);
                            String text = "NPC "+npcList.get(npcIndex).name+" podniósł broń "+weaponsList.get(j).name+" "+"("+weaponsList.get(j).posX+","+weaponsList.get(j).posY+").";
                            GUI.display.append(text+"\n");
                            weaponsList.remove(j);
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
        //if the HP level of the NPCClasses.NPC is higher than 50% of maximal value program check if there's an enemy in attack range
        else {
            boolean inRange = false; //true if there is an enemy in range, otherwise false
            int targetHP = 999; //HP points of current target
            //loop checks if there's an enemy in range and saves the coordinates of the one with the list HP points
            for(int i = 0; i < npcList.size(); i++) {
                double distance = distanceCalc(npcList.get(i).posX, npcList.get(i).posY, npcList.get(npcIndex).posX, npcList.get(npcIndex).posY);
                if(distance > 0 && npcList.get(i).HP < targetHP && distance <= currentRange) {
                    targetDistance = distance;
                    targetHP = npcList.get(i).HP;
                    targetX = npcList.get(i).posX;
                    targetY = npcList.get(i).posY;
                    targetIndex = i;
                    inRange = true;
                }
            }
            //calling the method to deal damage to the target
            if(inRange) {
                damageDealer(npcIndex, targetIndex);
            }
            //if there's no one in range it looks for the closest target of travel (another NPCClasses.NPC or better weapon)
            else {
                //the loop finding the closest NPCClasses.NPC and saving its coordinates
                for(int i = 0; i < npcList.size(); i++) {
                    double distance = distanceCalc(npcList.get(i).posX, npcList.get(i).posY, npcList.get(npcIndex).posX, npcList.get(npcIndex).posY);
                    if(distance > 0 && distance < targetDistance) {
                        targetDistance = distance;
                        targetX = npcList.get(i).posX;
                        targetY = npcList.get(i).posY;
                    }
                }
                //this loop tries to find a weapon that is better than the one wielded by NPCClasses.NPC and is closer than the closest enemy
                //if it finds such weapon it saves its coordinates
                int tempQuality = 0;
                for(int i = 0; i < weaponsList.size(); i++) {//szuka broni o najwyższej jakości (jęsli dystans do pistoletu i snajperki jest ten sam-> pójdzie po snajperkę)
                    if(TerrainGenerator.terrainMap.get(weaponsList.get(i).posX).get(weaponsList.get(i).posY)==4){
                        break; //if wpn is in the zone, don't go for it
                    }
                    if (weaponsList.get(i).quality > npcList.get(npcIndex).weapon.quality) {
                        double distance = distanceCalc(weaponsList.get(i).posX, weaponsList.get(i).posY, npcList.get(npcIndex).posX, npcList.get(npcIndex).posY);
                        if (distance > 0 && distance < targetDistance) {
                            targetDistance = distance;
                            targetX = weaponsList.get(i).posX;
                            targetY = weaponsList.get(i).posY;
                            tempQuality = weaponsList.get(i).quality;
                        }
                        else if (distance == targetDistance && weaponsList.get(i).quality > tempQuality) {
                            targetDistance = distance;
                            targetX = weaponsList.get(i).posX;
                            targetY = weaponsList.get(i).posY;
                            tempQuality = weaponsList.get(i).quality;
                        }
                    }
                }
                //calling the method to move the NPCClasses.NPC in targets direction
                for(int i = 1; i <= currentStamina; i++) {
                    movement(targetX, targetY, npcList.get(npcIndex).posX, npcList.get(npcIndex).posY, npcIndex);
                    for(int j = 0; j < weaponsList.size(); j++) {
                        if (npcList.get(npcIndex).posX == weaponsList.get(j).posX && npcList.get(npcIndex).posY == weaponsList.get(j).posY) {
                            //podniesienie broni
                            npcList.get(npcIndex).weapon = weaponsList.get(j);
                            String text = "NPC "+npcList.get(npcIndex).name+" podniósł broń "+weaponsList.get(j).name+" "+"("+weaponsList.get(j).posX+","+weaponsList.get(j).posY+").";
                            GUI.display.append(text+"\n");
                            weaponsList.remove(j);
                            actionTaken = true;
                            break;
                        }
                    }
                    if(actionTaken) {
                        break;
                    }
                    for(int j = 0; j < medkitList.size(); j++) {
                        if (npcList.get(npcIndex).posX == medkitList.get(j)[0] && npcList.get(npcIndex).posY == medkitList.get(j)[1]) {
                            String text = "NPC "+npcList.get(npcIndex).name+" podniósł apteczkę! HP "+npcList.get(npcIndex).HP+"->";
                            if((npcList.get(npcIndex).HP += 30)>npcList.get(npcIndex).maxHP){
                                npcList.get(npcIndex).HP=npcList.get(npcIndex).maxHP;
                            }else{
                                npcList.get(npcIndex).HP += 30;
                            }
                            text = text + npcList.get(npcIndex).HP;
                            GUI.display.append(text+"\n");
                            medkitList.remove(j);
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

    /**
     * Method used to calculate the distance between points on the map using simple Euclidean distance
     * @param targetX X coordinate of the target
     * @param targetY Y coordinate of the target
     * @param x x coordinate of the point from which we are calculating the distance
     * @param y y coordinate of the point from which we are calculating the distance
     * @return returns the distance calculated in double format
     */
    public static double distanceCalc(int targetX, int targetY, int x, int y) {
        return sqrt(abs(x - targetX) * abs(x - targetX) + abs(y - targetY) * abs(y - targetY));
    }

    /**
     * Method used to change the coordinates of the NPC
     * @param targetX X coordinate of NPC's destination
     * @param targetY Y coordinate of NPC's destination
     * @param x X coordinate of NPC's position
     * @param y Y coordinate of NPC's position
     * @param npcIndex index of NPC from npcList for which we do movement decisions
     */
    public static void movement(int targetX, int targetY, int x, int y, int npcIndex) {
        //targets coordinates are saved as targetX, targetY
        int moveX = npcList.get(npcIndex).posX;
        int moveY = npcList.get(npcIndex).posY;

        if(targetX<0||targetY<0){
            return; 
        }
        boolean isEmpty = true;
        if(targetX > x) {
            moveX++;
        }
        else if(targetX < x) {
            moveX--;
        }
        if(targetY < y){
            moveY--;
        }
        else if(targetY > y){
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
        }
    }

    /**
     * Method used to deal damage to another NPC
     * @param indexAttacker index of NPC from npcList which attacks the other NPC
     * @param indexTarget index of NPC from npcList which is getting attacked by the other NPC
     */
    public static void damageDealer(int indexAttacker, int indexTarget) {
        int damage = (int) (npcList.get(indexTarget).HP - npcList.get(indexAttacker).weapon.Attack(npcList.get(indexTarget).HP));
        String text = "NPC "+npcList.get(indexAttacker).name+"("+npcList.get(indexAttacker).posX+","+npcList.get(indexAttacker).posY+") atakuje NPC "
                +npcList.get(indexTarget).name+"("+npcList.get(indexTarget).posX+","+npcList.get(indexTarget).posY+") używając "
                +npcList.get(indexAttacker).weapon.name+" (DMG:"+damage+")"+
                "HP celu spada z "+npcList.get(indexTarget).HP+" na: ";
        //Special ability of spy
        if(Objects.equals(npcList.get(indexTarget).symbol, "A") && (int) (Math.random() * (10)) > 6){
            text = "NPC "+npcList.get(indexAttacker).name+"("+npcList.get(indexAttacker).posX+","+npcList.get(indexAttacker).posY+") atakuje NPC "
                    +npcList.get(indexTarget).name+"("+npcList.get(indexTarget).posX+","+npcList.get(indexTarget).posY+") używając "
                    +npcList.get(indexAttacker).weapon.name+ "; NPC " + npcList.get(indexTarget).name + " unika obrazen.";
        }
        else{
            //Special ability of soldier
            if(Objects.equals(npcList.get(indexAttacker).symbol, "G") && distanceCalc(npcList.get(indexAttacker).posX, npcList.get(indexAttacker).posY, npcList.get(indexTarget).posX, npcList.get(indexTarget).posY) < 2){
                text = "NPC "+npcList.get(indexAttacker).name+"("+npcList.get(indexAttacker).posX+","+npcList.get(indexAttacker).posY+") atakuje krytycznie NPC "
                        +npcList.get(indexTarget).name+"("+npcList.get(indexTarget).posX+","+npcList.get(indexTarget).posY+") używając "
                        +npcList.get(indexAttacker).weapon.name+" (DMG:"+damage * 1.20 +")"+
                        "HP celu spada z "+npcList.get(indexTarget).HP+" na: ";
                npcList.get(indexTarget).HP -= damage * 1.20;
            }
            //default attack
            else{

                npcList.get(indexTarget).HP -= damage;
            }
            text = text + npcList.get(indexTarget).HP;
        }
        if (npcList.get(indexTarget).HP <= 0) {
            npcList.remove(indexTarget);
            text = text + " NPC "+indexTarget + " został zabity!\n";
        }else{text = text + "\n";}
        GUI.display.append(text);
    }
}
