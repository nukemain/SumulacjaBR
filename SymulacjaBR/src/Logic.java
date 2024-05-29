import NPCClasses.*;
import javax.swing.*;
import java.awt.*;

import WeaponClasses.Weapon;

import java.util.*;
import java.util.List;

import static java.lang.Math.abs;
import static java.lang.Math.sqrt;



public class Logic {
    //Lists required for logic to function
    static List<NPC> npcList = new ArrayList<>();
    static List<Weapon> weaponsList = new ArrayList<>();
    static List<int[]> medkitList = new ArrayList<>();



    static void Symulacja(int sizeX,int sizeY,int NPCcount){
        //required for gui
        JFrame frame = new JFrame();
        JPanel panel = new JPanel();
        JLabel[][] labelGrid = new JLabel[sizeX][sizeY];
        //okienko
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setTitle("https://open.spotify.com/track/49X0LAl6faAusYq02PRAY6?si=79bb6030809d4eda");
        frame.setResizable(false);
        ImageIcon logo = new ImageIcon("logo.png");
        panel= new JPanel(new GridLayout(sizeX,sizeY));
        frame.setIconImage(logo.getImage());

        for(int y=0;y<sizeY;y++){
            for(int x=0;x<sizeX;x++){
                JLabel label = new JLabel();
                label.setOpaque(true);
                //label.setText("[("+x+","+y+")]");
                labelGrid[x][y] = label;
                panel.add(label);
            }
        }

        //ok
        String[][] map = Spawning.createMap(sizeX,sizeY);
        Spawning.spawnNPCs(sizeX,sizeY,NPCcount,map, npcList);//Logic required for spawning NPCClasses.NPC's
        Spawning.spawnWeapons(map,sizeX,sizeY,NPCcount, weaponsList);//Spawning weapons on the map
        Spawning.spawnMedkits(map,sizeX,sizeY,NPCcount, medkitList);//Spawning medkits on the map

        frame.add(panel);
        frame.setVisible(true);

        while (npcList.size() > 1){

            System.out.println(); //pusta linijka potrzebna do formatowania

            for(int y=0;y<sizeY;y++){
                for(int x=0;x<sizeX;x++){
                    System.out.print(map[y][x]);
                    labelGrid[x][y].setText(map[y][x]);
                    if(map[y][x].equals("[ ]")){
                       labelGrid[x][y].setBackground(Color.gray);
                    }
                    else if(map[y][x].equals("[H]")||map[y][x].equals("[R]")||map[y][x].equals("[S]")||map[y][x].equals("[+]")){
                        labelGrid[x][y].setBackground(Color.BLACK);
                    }
                    else{labelGrid[x][y].setBackground(Color.white);}
                }
                System.out.println();
            }


            System.out.println("Press any key to continue...");
            try{System.in.read();}
            catch(Exception e){}
            for (int i=0;i<npcList.size();i++){
                decisionMaker(i);
            }
            map = Spawning.updateMap(sizeX,sizeY,npcList,weaponsList,medkitList);
            //to samo co system("pause")
        }
        System.out.println("Winner is " + npcList.getFirst().index);
    }
    public static void decisionMaker(int npcIndex) {

        //Dane potrzebne do pracy programu:
        int targetX = -1;               //współrzędna x celu
        int targetY = -1;               //współrzędna y celu
        double targetDistance = 999;    //odległość od celu
        int targetIndex = -1;           //Numer celu do strzału w tabeli npc lub linijki w pliku potem
        boolean actionTaken = false;

        //sprawdzam czy HP mniejsze od 50% i jeśli tak to szuka najbliższej apteczki
        if((( (double) npcList.get(npcIndex).HP / npcList.get(npcIndex).maxHP) < 0.5) && !medkitList.isEmpty()) {
            //the loop finding the closest aid kit if HP under 50% and saving its coordinates
            for(int i = 0; i < medkitList.size(); i++) {
                double distance = distanceCalc(medkitList.get(i)[0],medkitList.get(i)[1], npcList.get(npcIndex).posX, npcList.get(npcIndex).posY);
                if(distance > 0 && distance < targetDistance) {
                    targetDistance = distance;
                    targetX = medkitList.get(i)[0];
                    targetY = medkitList.get(i)[1];
                    targetIndex = i;
                }
            }
            //calling the method to move the target in direction of target
            for(int i = 1; i <= npcList.get(npcIndex).stamina; i++) {
                movement(targetX, targetY, npcList.get(npcIndex).posX, npcList.get(npcIndex).posY, npcIndex);
                if(npcList.get(npcIndex).posX == targetX && npcList.get(npcIndex).posY == targetY) {//if on target...
                    for(int j = 0; j < medkitList.size(); j++) {//...pick up the medkit
                        if (npcList.get(npcIndex).posX == medkitList.get(j)[0] && npcList.get(npcIndex).posY == medkitList.get(j)[1]) {
                            System.out.print("\nNPCClasses.NPC "+npcList.get(npcIndex).index+" podniósł apteczkę! HP "+npcList.get(npcIndex).HP+"->");
                            if((npcList.get(npcIndex).HP += 30)>npcList.get(npcIndex).maxHP){
                                npcList.get(npcIndex).HP=npcList.get(npcIndex).maxHP;
                            }else{
                                npcList.get(npcIndex).HP += 30; //todo: zwiększyć wartość 30 ustawione tak o narazie
                            }
                            System.out.print(npcList.get(npcIndex).HP);
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
                            System.out.println("NPCClasses.NPC "+npcList.get(npcIndex).index+" podniósł broń "+weaponsList.get(j).name+" "+"("+weaponsList.get(j).posX+","+weaponsList.get(j).posY+").");
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
                if(distance > 0 && npcList.get(i).HP < targetHP && distance <= npcList.get(npcIndex).weapon.range) {
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
                for(int i = 1; i <= npcList.get(npcIndex).stamina; i++) {
                    movement(targetX, targetY, npcList.get(npcIndex).posX, npcList.get(npcIndex).posY, npcIndex);
                    for(int j = 0; j < weaponsList.size(); j++) {
                        if (npcList.get(npcIndex).posX == weaponsList.get(j).posX && npcList.get(npcIndex).posY == weaponsList.get(j).posY) {
                            //podniesienie broni
                            npcList.get(npcIndex).weapon = weaponsList.get(j);
                            System.out.println("NPCClasses.NPC "+npcList.get(npcIndex).index+" podniósł broń "+weaponsList.get(j).name+" "+"("+weaponsList.get(j).posX+","+weaponsList.get(j).posY+").");
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
                            System.out.print("\nNPCClasses.NPC "+npcList.get(npcIndex).index+" podniósł apteczkę! HP "+npcList.get(npcIndex).HP+"->");
                            if((npcList.get(npcIndex).HP += 30)>npcList.get(npcIndex).maxHP){
                                npcList.get(npcIndex).HP=npcList.get(npcIndex).maxHP;
                            }else{
                                npcList.get(npcIndex).HP += 30; //todo: zwiększyć wartość 30 ustawione tak o narazie
                            }
                            System.out.print(npcList.get(npcIndex).HP);
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
    //TODO: prevent NPCClasses.NPC from moving to the space occupied by the other NPCClasses.NPC
    public static void movement(int targetX, int targetY, int x, int y, int npcIndex) {
        //TODO: zabezpieczyć przed wchodzeniem na NPCClasses.NPC
        //targets coordinates are saved as targetX, targetY
        //proponuje, żeby NPCClasses.NPC mogli się poruszać po skosie, bo wtedy ścieżki, po których się będą poruszać będą bardziej naturalne
        int moveX = npcList.get(npcIndex).posX;
        int moveY = npcList.get(npcIndex).posY;
        boolean isEmpty = true;
        System.out.print("\nNPCClasses.NPC "+npcList.get(npcIndex).index + " porusza się z ("+ npcList.get(npcIndex).posX+","+npcList.get(npcIndex).posY+") na ");
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
            System.out.print("("+ npcList.get(npcIndex).posX+","+npcList.get(npcIndex).posY+")");
        }
        else{
            System.out.print("do nikąd -> docelowe pole ruchu jest już zajęte!");
            //System.out.println("Space occupied");
            //Potencjalnie sprawdzimy czy ma więcej staminy, jak nie to musi go zaatakować, bo inaczej sam zostanie zaatakowany po następnym ruchu
            //Jeżeli jednak ma więcej staminy to powinien próbować uciec po przekątnej w stronę w którą się kierował
            //Bez znaczenia czy w góre po przekątnej czy w dół
            //TODO: co chcemy robić, gdy na drodze NPCClasses.NPC stanie przeciwnik?
            // odp: stoi w miejscu, w następnej "turze" będą sie napierdalać
        }
    }
    public static void damageDealer(int indexAttacker, int indexTarget){
        //zamiast koordynatów jest targetIndex jako index w tabeli/linijka w pliku
        System.out.print("\nNPCClasses.NPC "+npcList.get(indexAttacker).index+"("+npcList.get(indexAttacker).posX+","+npcList.get(indexAttacker).posY+") atakuje NPCClasses.NPC "
                +npcList.get(indexTarget).index+"("+npcList.get(indexTarget).posX+","+npcList.get(indexTarget).posY+") używając "
                +npcList.get(indexAttacker).weapon.name+" (DMG:"+npcList.get(indexAttacker).weapon.damage+")"+
                "HP celu spada z "+npcList.get(indexTarget).HP+" na: ");

        //^długaśny print do debugowania
        npcList.get(indexTarget).HP -= npcList.get(indexAttacker).weapon.damage;
        System.out.print(npcList.get(indexTarget).HP);
        if (npcList.get(indexTarget).HP <= 0) {
            npcList.remove(indexTarget);
            System.out.print(" NPCClasses.NPC "+indexTarget + " został zabity!");
        }
    }
}
