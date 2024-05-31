import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class FileSaver {
    public static void fileSaver(int size) {
        Scanner fileNameReader = new Scanner(System.in);
        System.out.println("Enter the name of the new save: ");
        String fileName = fileNameReader.nextLine();
        String realFileName = "SBR_Save_" + fileName + ".txt";
        try {
            File testFile = new File(realFileName);
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
            FileWriter myWriter = new FileWriter(realFileName);
            myWriter.write(Logic.npcList.size() + " " + size + "\n");
            for(int i = 0; i < Logic.npcList.size(); i++) {
                myWriter.write(Logic.npcList.get(i).index + " " + Logic.npcList.get(i).posX + " " + Logic.npcList.get(i).posY + " " + Logic.npcList.get(i).HP + " " + Logic.npcList.get(i).weapon.name + " " + Logic.npcList.get(i).symbol + "\n");
            }
            myWriter.write( Logic.weaponsList.size() + "\n");
            for(int i = 0; i < Logic.weaponsList.size(); i++) {
                myWriter.write(Logic.weaponsList.get(i).name + " " + Logic.weaponsList.get(i).posX + " " + Logic.weaponsList.get(i).posY + "\n");
            }
            myWriter.write( Logic.medkitList.size() + "\n");
            for(int i = 0; i < Logic.medkitList.size(); i++) {
                myWriter.write(Logic.medkitList.get(i)[0] + " " + Logic.medkitList.get(i)[1] + "\n");
            }
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}
