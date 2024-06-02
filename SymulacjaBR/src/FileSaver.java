
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

public class FileSaver {
    public static void fileSaver(int size) throws FileNotFoundException{
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
        int pickedOption = fileChooser.showSaveDialog(null);

        if (pickedOption == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String realFileName = fileToSave.getAbsolutePath();
            // Ensure the file has the correct extension
            if (!realFileName.endsWith(".txt")) {
                realFileName += ".txt";
            }
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
        } else {
            GUI.display.append("Anulowano proces zapisu pliku.\n");
        }
    }
}