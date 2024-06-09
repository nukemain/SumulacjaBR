import NPCClasses.*;
import WeaponClasses.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class FileReader {
    public static int npcCount; //the number of the NPCs stored in the loaded file

    //method that reads the data needed to recreate a saved simulation from .txt file
    public static void fileReader() throws FileNotFoundException {
        //create a fileChooser window
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Wybierz (lub stwórz) plik .txt do zapisu stanu symulacji");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        FileNameExtensionFilter extfilter = new FileNameExtensionFilter("Pliki .txt", "txt");
        fileChooser.setFileFilter(extfilter);
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        //edit the UI of fileCooser
        UIManager.put("FileChooser.openButtonText", "Otwórz plik");
        UIManager.put("FileChooser.cancelButtonText", "Anuluj");
        UIManager.put("FileChooser.saveButtonText", "Zapisz");
        UIManager.put("FileChooser.saveButtonToolTipText", "Zapisz plik");
        UIManager.put("FileChooser.fileNameLabelText", "Nazwa pliku:");
        UIManager.put("FileChooser.filesOfTypeLabelText", "Rozszerzenie pliku:");
        UIManager.put("FileChooser.lookInLabelText", "Szukaj w:");
        UIManager.put("FileChooser.saveInLabelText", "Zapisz w:");
        UIManager.put("FileChooser.folderNameLabelText", "Folder:");
        UIManager.put("FileChooser.cancelButtonToolTipText", "Zapisz w");
        SwingUtilities.updateComponentTreeUI(fileChooser);
        
        int pickedOption = fileChooser.showOpenDialog(null);
        if (pickedOption == JFileChooser.APPROVE_OPTION) {
            String dataString = null; //stores the String taken from .txt file using .nextLine() method
            int index = -1; //the index number of the NPC loaded from the file
            int posX = -1; //the x coordinates of the NPC loaded from the file
            int posY = -1; //the y coordinates of the NPC loaded from the file
            int HP = -1; //number of HP points that the loaded NPC currently should have
            String wpnName = null; //the name of the weapon loaded from the file
            String symbol = null; //symbol used to distinguish the class of the NPC loaded from the file
            Weapon wpn = null;
            int itemCount = -1; //number of the items of the given type (weapons or medkits) that should be spawned in the simulation
            Scanner fileNameReader = new Scanner(System.in);
            try {
                File dataFile = fileChooser.getSelectedFile();
                Scanner dataReader = new Scanner(dataFile);
                //part of code that reads the size of the map and number of NPCs from the file
                if (dataReader.hasNextLine()) {
                    dataString = dataReader.nextLine();
                }
                String[] dataArray = dataString.split(" ", 2);
                try {
                    if(Logic.size !=Integer.valueOf(dataArray[1])){
                        Logic.size = Integer.valueOf(dataArray[1]);
                        GUI.mainPanel = GUI.resetLabelGrid(GUI.mainPanel);
                    }
                    npcCount = Integer.valueOf(dataArray[0]);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid integer input");
                }
                Logic.npcList.clear();
                Logic.map = Spawning.createMap(Logic.size);
                Logic.npcList.clear();
                //the part of code that reads NPCs stats from the file
                for (int i = 0; i < npcCount; i++) {
                    if (dataReader.hasNextLine()) {
                        dataString = dataReader.nextLine();
                    }
                    String[] npcDataArray = dataString.split(" ", 6);
                    try {
                        index = Integer.valueOf(npcDataArray[0]);
                        posX = Integer.valueOf(npcDataArray[1]);
                        posY = Integer.valueOf(npcDataArray[2]);
                        HP = Integer.valueOf(npcDataArray[3]);
                        wpnName = npcDataArray[4];
                        symbol = npcDataArray[5];
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid integer input");
                    }
                    //the part of code used to create the objects of weapon subclasses
                    switch (wpnName) {
                        case "Knife" -> {
                            wpn = new Knife(posX, posY);
                        }
                        case "Handgun" -> {
                            wpn = new Handgun(posX, posY);
                        }
                        case "Rifle" -> {
                            wpn = new Rifle(posX, posY);
                        }
                        case "SniperRifle" -> {
                            wpn = new SniperRifle(posX, posY);
                        }
                        case "Shotgun" -> {
                            wpn = new Shotgun(posX, posY);
                        }
                        case "SMG" -> {
                            wpn = new SMG(posX, posY);
                        }
                    }
                    //the part of code used to create the objects of NPC subclasses and add them to the npcList
                    switch (symbol) {
                        case "Σ" -> Logic.npcList.add(new Soldier(index, posX, posY,  wpn));
                        case "μ" -> Logic.npcList.add(new Medic(index, posX, posY,  wpn));
                        case "Λ" -> Logic.npcList.add(new Scout(index, posX, posY,  wpn));
                        case "Θ" -> Logic.npcList.add(new Sniper(index, posX, posY,  wpn));
                        case "Ω" -> Logic.npcList.add(new Spy(index, posX, posY,  wpn));
                        default -> System.out.println("Wrong symbol");
                    }
                    Logic.npcList.get(i).HP = HP;
                }
                //the part of code that reads the number of weapons to spawn
                if (dataReader.hasNextLine()) {
                    dataString = dataReader.nextLine();
                }
                try {
                    itemCount = Integer.valueOf(dataString);
                } catch (NumberFormatException e) {
                }
                Logic.weaponsList.clear();
                //the part of code that reads the stats of the weapons to spawn
                //then creates the objects of weapon subclasses and adds them to the weaponsList
                for (int i = 0; i < itemCount; i++) {
                    if (dataReader.hasNextLine()) {
                        dataString = dataReader.nextLine();
                    }
                    String[] wpnDataArray = dataString.split(" ", 3);
                    try {
                        wpnName = wpnDataArray[0];
                        posX = Integer.valueOf(wpnDataArray[1]);
                        posY = Integer.valueOf(wpnDataArray[2]);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid integer input");
                    }
                    switch (wpnName) {
                        case "Handgun":
                            Logic.weaponsList.add(new Handgun(posX, posY));
                            Logic.map.get(posY).set(posX, "[H]");
                            break;
                        case "Rifle":
                            Logic.weaponsList.add(new Rifle(posX, posY));
                            Logic.map.get(posY).set(posX, "[R]");
                            break;
                        case "SniperRifle":
                            Logic.weaponsList.add(new SniperRifle(posX, posY));
                            Logic.map.get(posY).set(posX, "[S]");
                            break;
                        case "Shotgun":
                            Logic.weaponsList.add(new Shotgun(posX, posY));
                            Logic.map.get(posY).set(posX, "[B]");
                            break;
                        case "SMG":
                            Logic.weaponsList.add(new SMG(posX, posY));
                            Logic.map.get(posY).set(posX, "[U]");
                            break;
                    }
                }
                //the part of the code that reads the number of medkits to spawn
                if (dataReader.hasNextLine()) {
                    dataString = dataReader.nextLine();
                }
                try {
                    itemCount = Integer.valueOf(dataString);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid integer input");
                }
                Logic.medkitList.clear();
                //the part of the code that reads the positions of medkits to spawn
                //then adds those positons to the medkitList
                for (int i = 0; i < itemCount; i++) {
                    if (dataReader.hasNextLine()) {
                        dataString = dataReader.nextLine();
                    }
                    String[] medDataArray = dataString.split(" ", 2);
                    try {
                        posX = Integer.valueOf(medDataArray[0]);
                        posY = Integer.valueOf(medDataArray[1]);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid integer input");
                    }
                    Logic.map.get(posY).set(posX, "[+]");
                    Logic.medkitList.add(new int[]{posX, posY});
                }
                TerrainGenerator.terrainMap.clear();
                //the part of the code used to read and set the type of terrain for every square on the map
                for (int y = 0; y < Logic.size; y++) {
                    if (dataReader.hasNextLine()) {
                        dataString = dataReader.nextLine();
                        TerrainGenerator.terrainMap.add(new ArrayList<>());
                    }
                    String[] terrainDataArray = dataString.split(" ", Logic.size);
                    try {
                        for(int x = 0; x < Logic.size; x++) {
                            TerrainGenerator.terrainMap.get(y).add(Integer.valueOf(terrainDataArray[x]));
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid integer input");
                    }
                }
                dataReader.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }else{
            GUI.display.append("Anulowano proces wczytu pliku.\n");
        }
    }
}
