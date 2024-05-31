import NPCClasses.*;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

import WeaponClasses.Weapon;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

import static java.lang.Math.abs;
import static java.lang.Math.sqrt;



public class Logic {
    //Lists required for logic to function
    static List<NPC> npcList = new ArrayList<>();
    static List<Weapon> weaponsList = new ArrayList<>();
    static List<int[]> medkitList = new ArrayList<>();

    //required for gui
    static JLabel labelInfo = new JLabel();

    static List<String> labelInfoText = new ArrayList<>();
    static JTextArea display = new JTextArea ( 16, 58 );
    //required for pausing
    static boolean buttonPressed = false;
    static boolean buttonHeld = false;
    static final Object lock = new Object();

    static String[][] map;

    static void Symulacja(int size, int NPCcount){
        //required for gui
        JFrame frame = new JFrame();
        JPanel panel = new JPanel();

        ImageIcon medkit = new ImageIcon("medpack.png");

        Border borderDefault = BorderFactory.createLineBorder(Color.black, 1);
        Border borderNPC = BorderFactory.createLineBorder(Color.red, 1);
        Border borderWeapon = BorderFactory.createLineBorder(Color.yellow, 1);
        Border borderMedkit = BorderFactory.createLineBorder(Color.green , 1);
        int cellSize = 30;//zmiana tej wartości zmienia rozmiar okienka,ikonek,skalowania obrazów - ogólnie z tego co widzę to 30 to idealna liczba
        // todo inna wartość? chyba nie ale można robić testy, jest 3:30 w nocy i nie chce mi sie teraz tego szukać

        JLabel[][] labelGrid = new JLabel[size][size];
        JButton buttonTop = new JButton("Następna tura");
        //================================================
        //code needed to make the "Następna tura" button work
        //code below is responsible for pressing AND holding down the button - user can hold down the button to continue the simulation
        buttonTop.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                buttonHeld = true;
                //czarna magia ze stack overflow, nie do końca rozumiem ale działa //todo
                new Thread(new Runnable() {
                    public void run() {
                        while (buttonHeld) {
                            synchronized (lock) {
                                buttonPressed = true;
                                lock.notify();
                            }
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException ignored) {}
                        }
                    }
                }).start();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                buttonHeld = false; //reset the buttonHeld state
            }
        });
        //================================================


        //================================================
        //code needed to make the "Zapisz stan planszy" button work
        JButton buttonMid = new JButton("Zapisz stan planszy");
        buttonMid.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("zapis");
                //todo
            }
        });
        //================================================

        //================================================
        //code needed to make the "Wczytaj stan planszy" button work
        JButton buttonBot= new JButton("Wczytaj stan planszy");
        buttonBot.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("wczyt");
                //todo:
            }
        });
        //================================================


        //================================================
        //main window
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(200+cellSize*size, 150+cellSize*size);
        frame.setTitle("Symulacja bttle royale");
        frame.setResizable(false);
        ImageIcon logo = new ImageIcon("logo.png");
        panel= new JPanel(new GridLayout(size,size));
        frame.setIconImage(logo.getImage());
        //================================================

        //================================================
        //main grid of labels
        for(int y=0;y<size;y++){
            for(int x=0;x<size;x++){
                JLabel label = new JLabel();
                label.setOpaque(true);
                label.setBorder(borderDefault);
                labelGrid[x][y] = label;
                panel.add(label);
                //================================================
                //code responsible for changing the text in labelInfo when hovering mouse over a tile on the labelGrid
                label.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        //getting the X and Y coordinates of the label on the grid -> coordinates correspond to values being used in the rest of the simulation's logic
                        int labelPosX=-1;
                        int labelPosY=-1;
                        for (int i = 0; i < size; i++) {
                            for (int j = 0; j < size; j++) {
                                if (labelGrid[i][j] == label) {
                                    labelPosX = i;
                                    labelPosY = j;
                                    break;
                                }
                            }
                        }
                        labelInfoText.add("Puste pole planszy.");
                        for (int i = 0; i < npcList.size(); i++) {
                            if(npcList.get(i).posX==labelPosX && npcList.get(i).posY==labelPosY){
                                labelInfoText.clear();
                                labelInfoText.add("NPC ID: "+npcList.get(i).index);
                                labelInfoText.add("Max. HP: "+npcList.get(i).maxHP);
                                labelInfoText.add("Obecne HP: "+npcList.get(i).HP);
                                labelInfoText.add("Broń: "+npcList.get(i).weapon.name);
                                labelInfoText.add("Zadawane obrażenia: "+npcList.get(i).weapon.damage);
                                labelInfoText.add("Zasięg ataku: "+(npcList.get(i).weapon.range/sqrt(2)));
                                break;
                            }
                        }
                        for (int i = 0; i < weaponsList.size(); i++) {
                            if(weaponsList.get(i).posX==labelPosX && weaponsList.get(i).posY==labelPosY){
                                labelInfoText.clear();
                                labelInfoText.add("Broń: "+weaponsList.get(i).name);
                                labelInfoText.add("Zadawane obrażenia: "+weaponsList.get(i).damage);
                                labelInfoText.add("Zasięg Broni: "+(weaponsList.get(i).range/sqrt(2)));
                                break;
                            }
                        }
                        for (int i = 0; i < medkitList.size(); i++) {
                            if(medkitList.get(i)[0]==labelPosX && medkitList.get(i)[1]==labelPosY){
                                labelInfoText.clear();
                                labelInfoText.add("Medkit: +30HP");
                                break;
                            }
                        }

                        labelInfo.setText(labelTextWrapper(labelInfoText));
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        labelInfoText.clear();
                        labelInfo.setText("<html>Nie zaznaczono<br>pola planszy.</html>");
                    }
                });
                //================================================
            }
        }
        //================================================

        //================================================
        //code responsible for spawning NPC's,Medkits and weapons if we are not loading data from a saved file
        if(map == null) {
            map = Spawning.createMap(size);
        }
        if (npcList.isEmpty()) {
            Spawning.spawnNPCs(size, NPCcount, map, npcList);//Logic required for spawning NPCClasses.NPC's
        }
        if (weaponsList.isEmpty()) {
            Spawning.spawnWeapons(map,size,NPCcount, weaponsList);//Spawning weapons on the map //todo remake methods to only use one size variable
        }
        if (weaponsList.isEmpty()) {
            Spawning.spawnMedkits(map,size, NPCcount, medkitList);//Spawning medkits on the map
        }
        //================================================

        String[][] map = Spawning.createMap(size);
        Spawning.spawnNPCs(size,NPCcount,map, npcList);//Logic required for spawning NPCClasses.NPC's
        Spawning.spawnWeapons(map,size,NPCcount, weaponsList);//Spawning weapons on the map
        Spawning.spawnMedkits(map,size,NPCcount, medkitList);//Spawning medkits on the map
        //labels
        labelInfo.setMinimumSize(new Dimension(buttonTop.getWidth(), buttonTop.getWidth()));
        labelInfo.setPreferredSize(new Dimension(buttonTop.getWidth(), buttonTop.getWidth()));
        labelInfo.setMaximumSize(new Dimension(buttonTop.getWidth(), buttonTop.getWidth()));


        //right panel
        JPanel panelRight =  new JPanel(new GridLayout(4,1));
        //add buttons to the right panel
        panelRight.add(buttonTop);
        panelRight.add(buttonMid);
        panelRight.add(buttonBot);
        panelRight.add(labelInfo);
        display.setEditable (false);

        JScrollPane bottomScrollPane = new JScrollPane(display);
        bottomScrollPane.setVerticalScrollBarPolicy ( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );
        bottomScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        //bottomScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);



        bottomScrollPane.setPreferredSize(new Dimension(frame.getWidth(), 150));
        //add everything to the main window frame
        frame.add(bottomScrollPane, BorderLayout.PAGE_END);
        frame.add(panel, BorderLayout.CENTER);
        frame.add(panelRight, BorderLayout.EAST);
        //display the frame
        frame.setVisible(true);

        int tura=0;
        display.append("Wciśnij i przytrzymaj przycisk \"Nastepna tura\" aby aktywować automatyczne wykonywanie ruchów!\n");
        while (npcList.size() > 1){
            tura++;
            //================================================
            //update the map
            Spawning.updateMap(size,size,npcList,weaponsList,medkitList);
            //================================================


            //================================================
            //clear the panel, clear
            for(int y=0;y<size;y++){
                for(int x=0;x<size;x++){
                    labelGrid[x][y].setIcon(null);
                    labelGrid[x][y].setBackground(Color.gray);
                    labelGrid[x][y].setBorder(borderDefault);
                }
            }
            //================================================

            //================================================
            //"print" data to the labels
            for(int i=0;i<npcList.size();i++){
                labelGrid[npcList.get(i).posX][npcList.get(i).posY].setIcon(resizeImg(npcList.get(i).icon,cellSize,cellSize));
                labelGrid[npcList.get(i).posX][npcList.get(i).posY].setBorder(borderNPC);
            }
            for(int i=0;i<weaponsList.size();i++){
                labelGrid[weaponsList.get(i).posX][weaponsList.get(i).posY].setIcon(resizeImg(weaponsList.get(i).icon,cellSize,cellSize));
                labelGrid[weaponsList.get(i).posX][weaponsList.get(i).posY].setBorder(borderWeapon);
            }
            for(int i=0;i<medkitList.size();i++){
                labelGrid[medkitList.get(i)[0]][medkitList.get(i)[1]].setIcon(resizeImg(medkit,cellSize,cellSize));
                labelGrid[medkitList.get(i)[0]][medkitList.get(i)[1]].setBorder(borderMedkit);
            }
            //================================================

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
            display.append("================================================================================================================================================================================================\n");
            display.append("[Tura nr"+ tura +"]\n");

            //================================================
            // do "logic" for each npc in npcList
            for (int i=0;i<npcList.size();i++){
                decisionMaker(i);
            }
            //================================================

        }
        Spawning.updateMap(size,size,npcList,weaponsList,medkitList);
        display.append("Wygrywa NPC o ID: "+ npcList.getFirst().index+"\n");
        display.append("Zamknij okienko aby zakończyc symulację!\n");
        labelGrid[npcList.getFirst().posX][npcList.getFirst().posY].setBackground(Color.yellow);

    }


    public static void decisionMaker(int npcIndex) {

        int targetX = -1;
        int targetY = -1;
        double targetDistance = 999;
        int targetIndex = -1;
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
                            String text = "NPC "+npcList.get(npcIndex).index+" podniósł apteczkę! HP "+npcList.get(npcIndex).HP+"->";
                            if((npcList.get(npcIndex).HP += 30)>npcList.get(npcIndex).maxHP){
                                npcList.get(npcIndex).HP=npcList.get(npcIndex).maxHP;
                            }else{
                                npcList.get(npcIndex).HP += 30; //todo: zwiększyć wartość 30 ustawione tak o narazie
                            }
                            text = text + npcList.get(npcIndex).HP;
                            display.append(text+"\n");
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
                            String text = "NPC "+npcList.get(npcIndex).index+" podniósł broń "+weaponsList.get(j).name+" "+"("+weaponsList.get(j).posX+","+weaponsList.get(j).posY+").";
                            display.append(text+"\n");
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
                            String text = "NPC "+npcList.get(npcIndex).index+" podniósł broń "+weaponsList.get(j).name+" "+"("+weaponsList.get(j).posX+","+weaponsList.get(j).posY+").";
                            display.append(text+"\n");
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
                            String text = "NPC "+npcList.get(npcIndex).index+" podniósł apteczkę! HP "+npcList.get(npcIndex).HP+"->";
                            if((npcList.get(npcIndex).HP += 30)>npcList.get(npcIndex).maxHP){
                                npcList.get(npcIndex).HP=npcList.get(npcIndex).maxHP;
                            }else{
                                npcList.get(npcIndex).HP += 30; //todo: zwiększyć wartość 30 ustawione tak o narazie
                            }
                            text = text + npcList.get(npcIndex).HP;
                            display.append(text+"\n");
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
        String text = "NPC "+npcList.get(indexAttacker).index+"("+npcList.get(indexAttacker).posX+","+npcList.get(indexAttacker).posY+") atakuje NPC "
                +npcList.get(indexTarget).index+"("+npcList.get(indexTarget).posX+","+npcList.get(indexTarget).posY+") używając "
                +npcList.get(indexAttacker).weapon.name+" (DMG:"+npcList.get(indexAttacker).weapon.damage+")"+
                "HP celu spada z "+npcList.get(indexTarget).HP+" na: ";
        npcList.get(indexTarget).HP -= npcList.get(indexAttacker).weapon.damage;
        text = text + npcList.get(indexTarget).HP;
        if (npcList.get(indexTarget).HP <= 0) {
            npcList.remove(indexTarget);
            text = text + " NPC "+indexTarget + " został zabity!\n";
        }else{text = text + "\n";}
        display.append(text);
    }


    //self-explanatory method name, takes in ImageIcon
    public static ImageIcon resizeImg(ImageIcon img, int width, int height) {
        Image newImg = img.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH); //object "ImageIcon" can't be resized, but object "Image" can -  so convert to that and resize
        return new ImageIcon(newImg);
    }
    //convert a list of strings into a single string ready to be "printed" in a label
    //labels have no built-in text wrapping but use standard HTMl tags to break text into lines
    public static String labelTextWrapper(List<String> text){
        String out = "<html>";
        for (int i = 0; i < text.size(); i++) {
            out = out + text.get(i) + "<br>";
        }
        return out + "</html>";
    }
}
