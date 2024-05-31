import NPCClasses.*;
import WeaponClasses.*;

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
        String dataString = null;
        int index = -1;
        int posX = -1;
        int posY = -1;
        int HP = -1;
        String wpnName = null;
        String symbol = null;
        Weapon wpn = null;
        int itemCount = -1;
        /*try {
            File testFile = new File("Test_File.txt");
            if (testFile.createNewFile()) {
                System.out.println("File created: " + testFile.getName());
                System.out.println("Absolute path: " + testFile.getAbsolutePath());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        try {
            FileWriter myWriter = new FileWriter("Test_File.txt");
            myWriter.write("5 20\n");
            myWriter.write("0 2 1 100 Knife Σ\n");
            myWriter.write("1 3 7 100 Handgun μ\n");
            myWriter.write("2 14 14 85 Rifle Λ\n");
            myWriter.write("3 9 16 30 SniperRifle Θ\n");
            myWriter.write("4 19 3 60 Shotgun Ω\n");
            myWriter.write("2\n");
            myWriter.write("Handgun 1 13\n");
            myWriter.write("Shotgun 12 12\n");
            myWriter.write("2\n");
            myWriter.write("10 10\n");
            myWriter.write("5 17\n");
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }*/
        Scanner fileNameReader = new Scanner(System.in);
        System.out.println("Enter the name of the file to read: ");
        String fileName = fileNameReader.nextLine();
        try {
            File dataFile = new File(fileName);
            Scanner dataReader = new Scanner(dataFile);
            if (dataReader.hasNextLine()) {
                dataString = dataReader.nextLine();
                System.out.println(dataString);
            }
            String[] dataArray = dataString.split(" ", 2);
            try {
                npcCount = Integer.valueOf(dataArray[0]);
                System.out.println("Converted integer: " + npcCount);
                size = Integer.valueOf(dataArray[1]);
                System.out.println("Converted integer: " + size);
            } catch (NumberFormatException e) {
                System.out.println("Invalid integer input");
            }
            System.out.println(dataString);
            Logic.map = Spawning.createMap(size);
            for(int i = 0; i < npcCount; i++) {
                if (dataReader.hasNextLine()) {
                    dataString = dataReader.nextLine();
                    System.out.println(dataString);
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
                        System.out.println(wpnName);
                        wpn = new Knife(wpnName, 15, 1, 0, posX, posY);
                    }
                    case "Handgun" -> {
                        System.out.println(wpnName);
                        wpn = new Handgun(wpnName, 25, 1, 1, posX, posY);
                    }
                    case "Rifle" -> {
                        System.out.println(wpnName);
                        wpn = new Rifle(wpnName, 35, 2, 2, posX, posY);
                    }
                    case "SniperRifle" -> {
                        System.out.println(wpnName);
                        wpn = new SniperRifle(wpnName, 40, 3, 3, posX, posY);
                    }
                    case "Shotgun" -> {
                        System.out.println(wpnName);
                        wpn = new Shotgun(wpnName, 50, 1, 2, posX, posY);
                    }
                }
                switch (symbol) {
                    case "Σ" ->
                            Logic.npcList.add(new Soldier(index, posX, posY, 150, 2, wpn, "Σ"));
                    case "μ" ->
                            Logic.npcList.add(new Medic(index, posX, posY, 100, 2, wpn, "μ"));
                    case "Λ" ->
                            Logic.npcList.add(new Scout(index, posX, posY, 90, 3, wpn, "Λ"));
                    case "Θ" ->
                            Logic.npcList.add(new Sniper(index, posX, posY, 100, 2, wpn, "Θ"));
                    case "Ω" ->
                            Logic.npcList.add(new Spy(index, posX, posY, 80, 2, wpn, "Ω"));
                    default -> System.out.println("Wrong symbol");
                }
                Logic.npcList.get(i).HP = HP;
            }
            if (dataReader.hasNextLine()) {
                dataString = dataReader.nextLine();
                System.out.println(dataString);
            }
            try {
                itemCount = Integer.valueOf(dataString);
                System.out.println("Converted integer: " + itemCount);
            } catch (NumberFormatException e) {
                System.out.println("Invalid integer input");
            }
            for(int i = 0; i < itemCount; i++) {
                if (dataReader.hasNextLine()) {
                    dataString = dataReader.nextLine();
                    System.out.println(dataString);
                }
                String[] wpnDataArray = dataString.split(" ", 3);
                try {
                    wpnName = wpnDataArray[0];
                    posX = Integer.valueOf(wpnDataArray[1]);
                    posY = Integer.valueOf(wpnDataArray[2]);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid integer input");
                }
                switch (wpnName){
                    case "Handgun":
                        Logic.weaponsList.add(new Handgun("Handgun", 25, 1,1, posX, posY));
                        Logic.map[posY][posX] = "[H]";
                        break;
                    case "Rifle":
                        Logic.weaponsList.add(new Rifle("Rifle", 35, 2,2, posX, posY));
                        Logic.map[posY][posX] = "[R]";
                        break;
                    case "SniperRifle":
                        Logic.weaponsList.add(new SniperRifle("SniperRifle", 40, 3,3, posX, posY));
                        Logic.map[posY][posX] = "[S]";
                        break;
                    case "Shotgun":
                        Logic.weaponsList.add(new Shotgun("Shotgun", 50, 1,2, posX, posY));
                        Logic.map[posY][posX] = "[B]";
                }
            }
            if (dataReader.hasNextLine()) {
                dataString = dataReader.nextLine();
                System.out.println(dataString);
            }
            try {
                itemCount = Integer.valueOf(dataString);
                System.out.println("Converted integer: " + itemCount);
            } catch (NumberFormatException e) {
                System.out.println("Invalid integer input");
            }
            for(int i = 0; i < itemCount; i++) {
                if (dataReader.hasNextLine()) {
                    dataString = dataReader.nextLine();
                    System.out.println(dataString);
                }
                String[] medDataArray = dataString.split(" ", 2);
                try {
                    posX = Integer.valueOf(medDataArray[0]);
                    posY = Integer.valueOf(medDataArray[1]);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid integer input");
                }
                Logic.map[posY][posX] = "[+]";
                Logic.medkitList.add(new int[]{posX, posY});
            }
            dataReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        /*File fileDeleter = new File("Test_File.txt");
        if (fileDeleter.delete()) {
            System.out.println("Deleted the file: " + fileDeleter.getName());
        } else {
            System.out.println("Failed to delete the file.");
        }*/
    }
    public static void main(String[] args) throws FileNotFoundException {
        fileReader();
    }
}
