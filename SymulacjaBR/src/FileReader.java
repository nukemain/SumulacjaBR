import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class FileReader {
    public static int npcCount;
    public static int size;
    public static void fileReader() throws FileNotFoundException {
        String controllerData = null;
        try {
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
            myWriter.write("5 10");
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        /*Scanner fileNameReader = new Scanner(System.in);
        System.out.println("Enter the name of the file to read: ");
        String fileName = fileNameReader.nextLine();*/
        try {
            File dataFile = new File("Test_File.txt");
            Scanner dataReader = new Scanner(dataFile);
            if (dataReader.hasNextLine()) {
                controllerData = dataReader.nextLine();
            }
            System.out.println(controllerData);
            dataReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        String[] dataArray = controllerData.split(" ", 2);
        try {
            npcCount = Integer.valueOf(dataArray[0]);
            System.out.println("Converted integer: " + npcCount);
            size = Integer.valueOf(dataArray[1]);
            System.out.println("Converted integer: " + size);
        } catch (NumberFormatException e) {
            System.out.println("Invalid integer input");
        }
        File fileDeleter = new File("Test_File.txt");
        if (fileDeleter.delete()) {
            System.out.println("Deleted the file: " + fileDeleter.getName());
        } else {
            System.out.println("Failed to delete the file.");
        }
    }
}
