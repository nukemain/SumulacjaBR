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
    public static int npcCount;
    public static int size;
    public static void fileReader() throws FileNotFoundException {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Wybierz (lub stwórz) plik .txt do zapisu stanu symulacji");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        FileNameExtensionFilter extfilter = new FileNameExtensionFilter("Pliki .txt", "txt");
        fileChooser.setFileFilter(extfilter);
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        UIManager.put("FileChooser.openButtonText", "Otwórz plik");
        UIManager.put("FileChooser.cancelButtonText", "Anuluj");
        UIManager.put("FileChooser.saveButtonText", "Zapisz");
        UIManager.put("FileChooser.saveButtonToolTipText", "Zapisz plik");
        //UIManager.put("FileChooser.cancelButtonToolTipText", "Anuluj v2");
        UIManager.put("FileChooser.fileNameLabelText", "Nazwa pliku:");
        UIManager.put("FileChooser.filesOfTypeLabelText", "Rozszerzenie pliku:");
        UIManager.put("FileChooser.lookInLabelText", "Szukaj w:");
        UIManager.put("FileChooser.saveInLabelText", "Zapisz w:");
        UIManager.put("FileChooser.folderNameLabelText", "Folder:");
        UIManager.put("FileChooser.cancelButtonToolTipText", "Zapisz w");

        SwingUtilities.updateComponentTreeUI(fileChooser);
        int pickedOption = fileChooser.showOpenDialog(null);
        if (pickedOption == JFileChooser.APPROVE_OPTION) {
            String dataString = null;
            int index = -1;
            int posX = -1;
            int posY = -1;
            int HP = -1;
            String wpnName = null;
            String symbol = null;
            Weapon wpn = null;
            int itemCount = -1;
            Scanner fileNameReader = new Scanner(System.in);
            //System.out.println("Enter the name of the file to read: ");
            //String fileName = fileNameReader.nextLine();
            //String realFileName = "SBR_Save_" + fileName + ".txt";
            try {
                File dataFile = fileChooser.getSelectedFile();
                Scanner dataReader = new Scanner(dataFile);
                if (dataReader.hasNextLine()) {
                    dataString = dataReader.nextLine();
                    //System.out.println(dataString);
                }
                String[] dataArray = dataString.split(" ", 2);
                try {
                    npcCount = Integer.valueOf(dataArray[0]);
                    //System.out.println("Converted integer: " + npcCount);
                    size = Integer.valueOf(dataArray[1]);
                    //System.out.println("Converted integer: " + size);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid integer input");
                }
                //System.out.println(dataString);
                Logic.map.clear();
                Logic.map = Spawning.createMap(size);
                Logic.npcList.clear();
                for (int i = 0; i < npcCount; i++) {
                    if (dataReader.hasNextLine()) {
                        dataString = dataReader.nextLine();
                        //System.out.println(dataString);
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
                    switch (wpnName) {
                        case "Knife" -> {
                            //System.out.println(wpnName);
                            wpn = new Knife(wpnName, 15, 1, 0, posX, posY);
                        }
                        case "Handgun" -> {
                            //System.out.println(wpnName);
                            wpn = new Handgun(wpnName, 25, 1, 1, posX, posY);
                        }
                        case "Rifle" -> {
                            //System.out.println(wpnName);
                            wpn = new Rifle(wpnName, 35, 2, 2, posX, posY);
                        }
                        case "SniperRifle" -> {
                            //System.out.println(wpnName);
                            wpn = new SniperRifle(wpnName, 40, 3, 3, posX, posY);
                        }
                        case "Shotgun" -> {
                            //System.out.println(wpnName);
                            wpn = new Shotgun(wpnName, 50, 1, 2, posX, posY);
                        }
                        case "SMG" -> {
                            //System.out.println(wpnName);
                            wpn = new SMG(wpnName, 50, 1, 2, posX, posY);
                        }
                    }
                    switch (symbol) {
                        case "Σ" -> Logic.npcList.add(new Soldier(index, posX, posY, 150, 2, wpn, "Σ"));
                        case "μ" -> Logic.npcList.add(new Medic(index, posX, posY, 100, 2, wpn, "μ"));
                        case "Λ" -> Logic.npcList.add(new Scout(index, posX, posY, 90, 3, wpn, "Λ"));
                        case "Θ" -> Logic.npcList.add(new Sniper(index, posX, posY, 100, 2, wpn, "Θ"));
                        case "Ω" -> Logic.npcList.add(new Spy(index, posX, posY, 80, 2, wpn, "Ω"));
                        default -> System.out.println("Wrong symbol");
                    }
                    Logic.npcList.get(i).HP = HP;
                }
                if (dataReader.hasNextLine()) {
                    dataString = dataReader.nextLine();
                    //System.out.println(dataString);
                }
                try {
                    itemCount = Integer.valueOf(dataString);
                    //System.out.println("Converted integer: " + itemCount);
                } catch (NumberFormatException e) {
                    //System.out.println("Invalid integer input");
                }
                Logic.weaponsList.clear();
                for (int i = 0; i < itemCount; i++) {
                    if (dataReader.hasNextLine()) {
                        dataString = dataReader.nextLine();
                        //System.out.println(dataString);
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
                            Logic.weaponsList.add(new Handgun("Handgun", 25, 1, 1, posX, posY));
                            Logic.map.get(posY).set(posX, "[H]");
                            break;
                        case "Rifle":
                            Logic.weaponsList.add(new Rifle("Rifle", 35, 2, 2, posX, posY));
                            Logic.map.get(posY).set(posX, "[R]");
                            break;
                        case "SniperRifle":
                            Logic.weaponsList.add(new SniperRifle("SniperRifle", 40, 3, 3, posX, posY));
                            Logic.map.get(posY).set(posX, "[S]");
                            break;
                        case "Shotgun":
                            Logic.weaponsList.add(new Shotgun("Shotgun", 50, 1, 2, posX, posY));
                            Logic.map.get(posY).set(posX, "[B]");
                            break;
                        case "SMG":
                            Logic.weaponsList.add(new SMG("SMG", 50, 1, 2, posX, posY));
                            Logic.map.get(posY).set(posX, "[U]");
                            break;
                    }
                }
                if (dataReader.hasNextLine()) {
                    dataString = dataReader.nextLine();
                    //System.out.println(dataString);
                }
                try {
                    itemCount = Integer.valueOf(dataString);
                    //System.out.println("Converted integer: " + itemCount);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid integer input");
                }
                Logic.medkitList.clear();
                for (int i = 0; i < itemCount; i++) {
                    if (dataReader.hasNextLine()) {
                        dataString = dataReader.nextLine();
                        //System.out.println(dataString);
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
                dataReader.close();
            } catch (FileNotFoundException e) {
                //System.out.println("An error occurred.");
                e.printStackTrace();
            }
        /*File fileDeleter = new File("Test_File.txt");
        if (fileDeleter.delete()) {
            System.out.println("Deleted the file: " + fileDeleter.getName());
        } else {
            System.out.println("Failed to delete the file.");
        }*/
        }else{
            GUI.display.append("Anulowano proces wczytu pliku.\n");
        }
    }
    public static void main(String[] args) throws FileNotFoundException {
        fileReader();
    }
}
