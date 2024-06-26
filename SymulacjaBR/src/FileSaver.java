
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Class responsible for saving data on Simulation's state.
 */
public class FileSaver {
    /**
     * Method that saves the data needed to recreate a saved simulation from .txt file. Asks the user to pick (or create a new) file for the data to be saved into using a GUI window.
     * @param size size of the simulation's board
     * @throws FileNotFoundException
     */
    //method used to save the simulation in .txt file
    public static void fileSaver(int size) throws FileNotFoundException{
        //create a fileChooser window
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Wybierz (lub stwórz) plik .txt do zapisu stanu symulacji");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        FileNameExtensionFilter extfilter = new FileNameExtensionFilter("Pliki .txt", "txt");
        fileChooser.setFileFilter(extfilter);
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        //edit the UI of fileChooser
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

        int pickedOption = fileChooser.showSaveDialog(null);

        if (pickedOption == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String realFileName = fileToSave.getAbsolutePath();
            //make sure the picked file has the correct extension
            if (!realFileName.endsWith(".txt")) {
                realFileName += ".txt";
            }
            try {
                File file = new File(realFileName);
                if (file.createNewFile()) {
                    GUI.display.append("Stworzono plik: " + file.getName());
                    GUI.display.append("Znajduje się on w: " + file.getAbsolutePath());
                } else {
                    System.out.println("File already exists.");
                }
            } catch (IOException e) {
                GUI.display.append("Wystąpił błąd przy zapisie symulacji. Operacja nie powiodła się.");
                e.printStackTrace();
            }
            //saves all the data needed to recreate the simulation
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
                for(int y = 0; y < size; y++) {
                    for( int x = 0; x < size; x++) {
                        myWriter.write(TerrainGenerator.terrainMap.get(y).get(x).toString());
                        System.out.println(TerrainGenerator.terrainMap.get(y).get(x));
                        if(x != size - 1) {
                            myWriter.write(" ");
                        }
                    }
                    myWriter.write("\n");
                }
                myWriter.close();
                GUI.display.append("Poprawnie zapisano stan symulacji.");
            } catch (IOException e) {
                GUI.display.append("Wystąpił błąd przy zapisie symulacji. Operacja nie powiodła się.");
                e.printStackTrace();
            }
        } else {
            GUI.display.append("Anulowano proces zapisu pliku.\n");
        }
    }
}
