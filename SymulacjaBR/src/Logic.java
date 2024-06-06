import NPCClasses.*;
import javax.swing.*;
import java.awt.*;

import WeaponClasses.Knife;
import WeaponClasses.Weapon;

import java.io.IOException;
import java.util.*;
import java.util.List;

import static java.lang.Math.abs;
import static java.lang.Math.sqrt;



public class Logic {

    static int size = 1;
    static int NPCcount = 10;
    static JFrame SimulationFrame = new JFrame();
    //Lists required for logic to function
    static List<NPC> npcList = new ArrayList<>();
    static List<Weapon> weaponsList = new ArrayList<>();
    static List<int[]> medkitList = new ArrayList<>();

    public static int roundsCounter = 0;


    //required for pausing
    static boolean buttonPressed = false;
    static boolean buttonHeld = false;
    static final Object lock = new Object();

    //static String[][] map;
    static List<List<String>> map = new ArrayList<>();
    public static void main(String[] args) throws IOException {
        Symulacja();
        //WIELKA LISTA RZECZY DO ZROBIENIA
        //[TĄ SEKCJĘ MUSIMY ZROBIĆ]
        //done: https://discord.com/channels/1236752666273775667/1236752666781290557/1246940156083310732
        //done: teren -> zrobiony szumem perlina (pytać Piotra), myślę że tak z trzy rózne rodzaje terenu conajmniej, pełna współpraca z AI npc
        //done: wczytywanie plików
        //done: zapisywanie do plików
        //done: input od użytkownika do gui
        //done: zmniejszanie sie strefy - to do zrobienia po terenie - strefa jako rodzaj terenu który bije dmg temu co na tym stoi
        //done: gui do innej klasy
        //done: nazwy npc w printach na dole zamiast id
        //done: statystyki terenu w tym małym okienku pod przyciskami
        //done: statystyki specjalne npc         -||-
        //done:         -||-         broni       -||-
        //todo: rewrite spawningu (większa kontrola nad ilością respionej broni i medkitów - zamiast "losowa" liczba jak jest teraz)
        //imo jak rzeczy wyżej będą gotowe to mozna mówić o gotowym projekcie
    }

    static void Symulacja() throws IOException {
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

            //update the map
            //Spawning.updateMap(Logic.size,npcList,weaponsList,medkitList);
            //GUI.refreshGUIMap();

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
            //GUI.mainPanel.setVisible(false);
            Spawning.updateMap(Logic.size,npcList,weaponsList,medkitList);
            GUI.refreshGUIMap();
            if(roundsCounter%2==0){
            TerrainGenerator.ShrinkZone(Logic.size,Logic.size/2,Logic.size/2,roundsCounter);
            GUI.refreshTerrain();
            }
            //GUI.mainPanel.setVisible(true);

        }
        Spawning.updateMap(Logic.size,npcList,weaponsList,medkitList);
        GUI.refreshGUIMap();
        csvObject.dataAdder(npcList.size(), weaponsList.size(), medkitList.size());
        csvObject.csvWriter();
        GUI.SimulationGUIEnd(Logic.SimulationFrame);
    }


    public static void decisionMaker(int npcIndex) {

        int targetX = -1;
        int targetY = -1;
        double targetDistance = 999;
        int targetIndex = -1;
        boolean actionTaken = false;
        int currentTerrain = TerrainGenerator.terrainMap.get(npcList.get(npcIndex).posY).get(npcList.get(npcIndex).posX);
        double currentRange = npcList.get(npcIndex).weapon.range;
        int currentStamina = npcList.get(npcIndex).stamina;
        for(int i=0; i<medkitList.size();i++){
            if(TerrainGenerator.terrainMap.get(medkitList.get(i)[0]).get(medkitList.get(i)[1])==4){
                medkitList.remove(i);
            }
        }

        //Using their abilities every turn
        if(Objects.equals(npcList.get(npcIndex).symbol, "μ") || Objects.equals(npcList.get(npcIndex).symbol, "Θ")){
            npcList.get(npcIndex).Ability();
        }

        switch(currentTerrain) {
            case 0://desert
                if(currentStamina > 1) {
                    currentStamina -= 1;
                    //System.out.println(npcIndex + " ma zmniejszoną staminę");
                }
                break;
            case 2://forest
                currentRange = sqrt(2);
                //System.out.println(npcIndex + " ma zmniejszony zasięg");
                break;
            case 3://mountains
                if(!npcList.get(npcIndex).weapon.name.equals("Knife")) {
                    currentRange += 1;
                    //System.out.println(npcIndex + " ma zwiększony zasięg");
                }
                break;
            case 4: //zone
                npcList.get(npcIndex).HP -=10;
                if(npcList.get(npcIndex).HP<=0){
                    GUI.display.append(npcList.get(npcIndex).name+" umarł od strefy!\n");
                    npcList.remove(npcIndex);
                    return;
                }
                //próba dorobienia uciekania od strefy
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
                for(int i = 1; i <= currentStamina; i++) {
                    movement(targetX, targetY, npcList.get(npcIndex).posX, npcList.get(npcIndex).posY, npcIndex); //ucieczka ze strefy zawsze priorytetem
                }
                break;
        }

        //sprawdzam czy HP mniejsze od 50% i jeśli tak to szuka najbliższej apteczki
        if((( (double) npcList.get(npcIndex).HP / npcList.get(npcIndex).maxHP) < 0.5) && !medkitList.isEmpty()) {
            //the loop finding the closest aid kit if HP under 50% and saving its coordinates
            for(int i = 0; i < medkitList.size(); i++) {
                if(TerrainGenerator.terrainMap.get(medkitList.get(i)[0]).get(medkitList.get(i)[1])==4){
                    break; //if medkit is in the zone, dont go for it
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
                                npcList.get(npcIndex).HP += 30; //todo: zwiększyć wartość 30 ustawione tak o narazie
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
                //TODO: it could also be prevent from going through the loop if there's no weapon or no better weapon is present
                int tempQuality = 0;
                for(int i = 0; i < weaponsList.size(); i++) {//szuka broni o najwyższej jakości (jęsli dystans do pistoletu i snajperki jest ten sam-> pójdzie po snajperkę)
                    if(TerrainGenerator.terrainMap.get(weaponsList.get(i).posX).get(weaponsList.get(i).posY)==4){
                        break; //if wpn is in the zone, dont go for it
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
                                npcList.get(npcIndex).HP += 30; //todo: zwiększyć wartość 30 ustawione tak o narazie
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
    //method used to calculate the distance between points on the map
    public static double distanceCalc(int targetX, int targetY, int x, int y) {
        return sqrt(abs(x - targetX) * abs(x - targetX) + abs(y - targetY) * abs(y - targetY));
    }
    public static void movement(int targetX, int targetY, int x, int y, int npcIndex) {
        //TODO: zabezpieczyć przed wchodzeniem na NPCClasses.NPC
        //targets coordinates are saved as targetX, targetY
        //proponuje, żeby NPCClasses.NPC mogli się poruszać po skosie, bo wtedy ścieżki, po których się będą poruszać będą bardziej naturalne
        int moveX = npcList.get(npcIndex).posX;
        int moveY = npcList.get(npcIndex).posY;
        if(targetX<0||targetY<0){
            return; 
        }
        boolean isEmpty = true;
        //System.out.print("\nNPCClasses.NPC "+npcList.get(npcIndex).index + " porusza się z ("+ npcList.get(npcIndex).posX+","+npcList.get(npcIndex).posY+") na ");
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
            //System.out.print("("+ npcList.get(npcIndex).posX+","+npcList.get(npcIndex).posY+")");
        }
        else{
            //do nothing

            //System.out.print("do nikąd -> docelowe pole ruchu jest już zajęte!");
            //System.out.println("Space occupied");
            //Potencjalnie sprawdzimy czy ma więcej staminy, jak nie to musi go zaatakować, bo inaczej sam zostanie zaatakowany po następnym ruchu
            //Jeżeli jednak ma więcej staminy to powinien próbować uciec po przekątnej w stronę w którą się kierował
            //Bez znaczenia czy w góre po przekątnej czy w dół
            //TODO: co chcemy robić, gdy na drodze NPCClasses.NPC stanie przeciwnik?
            // odp: stoi w miejscu, w następnej "turze" będą sie napierdalać
        }
    }
    public static void damageDealer(int indexAttacker, int indexTarget) {
        int damage = (int) (npcList.get(indexTarget).HP - npcList.get(indexAttacker).weapon.Attack(npcList.get(indexTarget).HP));
        String text = "NPC "+npcList.get(indexAttacker).name+"("+npcList.get(indexAttacker).posX+","+npcList.get(indexAttacker).posY+") atakuje NPC "
                +npcList.get(indexTarget).name+"("+npcList.get(indexTarget).posX+","+npcList.get(indexTarget).posY+") używając "
                +npcList.get(indexAttacker).weapon.name+" (DMG:"+damage+")"+
                "HP celu spada z "+npcList.get(indexTarget).HP+" na: ";
        if(Objects.equals(npcList.get(indexTarget).symbol, "Ω") && (int) (Math.random() * (10)) > 6){
            text = "NPC "+npcList.get(indexAttacker).name+"("+npcList.get(indexAttacker).posX+","+npcList.get(indexAttacker).posY+") atakuje NPC "
                    +npcList.get(indexTarget).name+"("+npcList.get(indexTarget).posX+","+npcList.get(indexTarget).posY+") używając "
                    +npcList.get(indexAttacker).weapon.name+ "; NPC " + npcList.get(indexTarget).name + " unika obrazen.";
        }
        else{
            if(Objects.equals(npcList.get(indexAttacker).symbol, "Σ") && distanceCalc(npcList.get(indexAttacker).posX, npcList.get(indexAttacker).posY, npcList.get(indexTarget).posX, npcList.get(indexTarget).posY) < 2){
                text = "NPC "+npcList.get(indexAttacker).name+"("+npcList.get(indexAttacker).posX+","+npcList.get(indexAttacker).posY+") atakuje krytycznie NPC "
                        +npcList.get(indexTarget).name+"("+npcList.get(indexTarget).posX+","+npcList.get(indexTarget).posY+") używając "
                        +npcList.get(indexAttacker).weapon.name+" (DMG:"+damage * 1.20 +")"+
                        "HP celu spada z "+npcList.get(indexTarget).HP+" na: ";
                npcList.get(indexTarget).HP -= damage * 1.20;
            }
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
