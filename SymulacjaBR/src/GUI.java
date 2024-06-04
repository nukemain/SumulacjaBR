import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.Math.random;
import static java.lang.Math.sqrt;

public class GUI {
    static private final int cellSize = 30;
    static JLabel labelInfo = new JLabel();
    static List<String> labelInfoText = new ArrayList<>();
    static List<List<JLabel>> labelGrid = new ArrayList<>();
    static JPanel mainPanel = new JPanel();
    static private final Border borderDefault = BorderFactory.createLineBorder(Color.black, 1);
    static private final Border borderNPC = BorderFactory.createLineBorder(Color.red, 1);
    static private final Border borderWeapon = BorderFactory.createLineBorder(Color.orange, 1);
    static private final Border borderMedkit = BorderFactory.createLineBorder(Color.green, 1);
    static JButton buttonTop = new JButton("Następna tura");
    static JButton buttonMid = new JButton("Zapisz stan planszy");
    static JButton buttonBot = new JButton("Wczytaj stan planszy");
    static JButton buttonClose = new JButton("Zamknij program");
    static JButton buttonNewSim = new JButton("Zacznij nową symulację");
    static JPanel panelRight = new JPanel(new GridLayout(3, 1));

    static private ImageIcon medkit = new ImageIcon("medpack.png");
    static JTextArea display = new JTextArea(16, 58);

    //static  JFrame DataEntryFrame = new JFrame("User Input Window");



    //all the code responsible for the window showing the simulation
    public static void SimulationGUI(JFrame frame) {


        buttonClose.setVisible(false);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // Add window listener to print message on window closing
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int returnValue=-1;
                if(Logic.npcList.size() <= 1) {
                    //returnValue = JOptionPane.OK_OPTION;
                    System.exit(0);
                }else{
                    String[] buttons = {"Zamknij","Zapisz symulację i zamknij", "Anuluj"};
                    returnValue = JOptionPane.showOptionDialog(null, "Zamknięcie programu bezpowrotnie zakończy obecną symulację!", "Uwaga! - Symulacja w toku!",
                            JOptionPane.DEFAULT_OPTION, 1, null, buttons, buttons[0]);
                }

                switch (returnValue) {
                    case 0:
                        System.exit(0);
                        break;
                    case 1:
                        try {
                            FileSaver.fileSaver(Controller.size);
                            System.exit(0);
                        } catch (FileNotFoundException ex) {
                            JOptionPane.showMessageDialog(null, "Wystąpił błąd przy zapisywaniu - czy wybrano poprawny plik do zapisu?", "Błąd", JOptionPane.ERROR_MESSAGE);
                        }
                        break;
                    case 2:
                        break;
                    default:
                        break;
                }
            }
        });
        frame.setSize(200 + cellSize * 25, 150 + cellSize * 25);
        frame.setTitle("Symulacja battle royale");
        frame.setResizable(false);
        ImageIcon logo = new ImageIcon("logo.png");
        mainPanel = new JPanel(new GridLayout(Controller.size, Controller.size));
        frame.setIconImage(logo.getImage());

        mainPanel = resetLabelGrid(mainPanel);

        buttonClose.setVisible(true);
        panelRight.removeAll();
        mainPanel.setVisible(false);


        panelRight.add(buttonNewSim);
        panelRight.add(buttonBot);
        panelRight.add(buttonClose);

        labelInfo.setBorder(borderDefault);
        labelInfo.setText("<html>Najedź na pole planszy<br>aby zobaczyć związane z nim dane!</html>");
        display.setEditable(false);
        mainPanel.setBorder(borderDefault);

        JScrollPane bottomScrollPane = new JScrollPane(display);
        bottomScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        bottomScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        bottomScrollPane.setPreferredSize(new Dimension(frame.getWidth(), 150));

        frame.add(bottomScrollPane, BorderLayout.PAGE_END);
        frame.add(mainPanel, BorderLayout.CENTER);
        frame.add(panelRight, BorderLayout.EAST);
        panelRight.setVisible(true);

        buttonNewSim.addActionListener(e -> {
            int returnValue=-1;
            if(Logic.npcList.size() <= 1) {
                returnValue = JOptionPane.OK_OPTION;
            }else{
                String[] buttons = {"Kontynuuj", "Anuluj"};
                returnValue = JOptionPane.showOptionDialog(null, "To działanie bezpowrotnie zakończy obecną symulację!", "Uwaga! - Symulacja w toku!",
                        JOptionPane.OK_CANCEL_OPTION, 1, null, buttons, buttons[0]);
            }

            if (returnValue == JOptionPane.OK_OPTION) {


                int[] newParams = getUserSimulationInput();
                Controller.size = newParams[0];
                Controller.NPCcount = newParams[1];
                mainPanel = resetLabelGrid(mainPanel);
                mainPanel.setVisible(true);

                //change buttons visible on the right side
                GUI.panelRight.removeAll();
                buttonClose.setVisible(false);
                buttonClose.setVisible(false);

                GUI.panelRight.removeAll();
                GUI.panelRight.setLayout(new GridLayout(5, 1));
                buttonClose.setVisible(true);
                GUI.panelRight.add(buttonTop);
                GUI.panelRight.add(buttonMid);
                GUI.panelRight.add(buttonBot);
                GUI.panelRight.add(buttonNewSim);
                GUI.panelRight.add(labelInfo);

                //reset simulation
                GUI.panelRight.revalidate();
                GUI.panelRight.repaint();

                Logic.map.clear();
                Logic.npcList.clear();
                Logic.weaponsList.clear();
                Logic.medkitList.clear();
                TerrainGenerator.terrainMap.clear();

                //create a new simulation
                Logic.map = Spawning.createMap(Controller.size);
                TerrainGenerator.terrainGenerator(Controller.size);
                Spawning.spawnNPCs(Controller.size, Controller.NPCcount, Logic.map, Logic.npcList);//Logic required for spawning NPCClasses.NPC's
                Spawning.spawnWeapons(Logic.map, Controller.size, Controller.NPCcount, Logic.weaponsList);//Spawning weapons on the map
                Spawning.spawnMedkits(Logic.map, Controller.size, Controller.NPCcount, Logic.medkitList);
                //refresh map and gui
                Spawning.updateMap(Controller.size, Logic.npcList, Logic.weaponsList, Logic.medkitList);
                refreshGUIMap();
                refreshTerrain();
                CSVGenerator.dataReseter();
                display.setText("Wciśnij i przytrzymaj przycisk \"Nastepna tura\" aby aktywować automatyczne wykonywanie ruchów!\n");
                synchronized (Logic.lock) {
                    Logic.buttonPressed = true;
                    Logic.lock.notify();
                }
            }
        });
        frame.setVisible(true);

        display.append("Wciśnij i przytrzymaj przycisk \"Nastepna tura\" aby aktywować automatyczne wykonywanie ruchów!\n");

        buttonTop.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Logic.buttonHeld = true;
                new Thread(new Runnable() {
                    public void run() {
                        while (Logic.buttonHeld) {
                            synchronized (Logic.lock) {
                                Logic.buttonPressed = true;
                                Logic.lock.notify();
                            }
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException ignored) {
                            }
                        }
                    }
                }).start();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                Logic.buttonHeld = false;
            }
        });

        buttonMid.addActionListener(e -> {
            try {
                FileSaver.fileSaver(Controller.size);
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });
        buttonBot.addActionListener(e -> {
            String[] buttons = {"Kontynuuj", "Anuluj"};
            int returnValue = JOptionPane.showOptionDialog(null, "Wczytanie pliku symulacji zakończy obecną symulację!", "Uwaga!",
                    JOptionPane.OK_CANCEL_OPTION, 1, null, buttons, buttons[0]);
            if (returnValue == JOptionPane.OK_OPTION) {
                try {
                    FileReader.fileReader();
                    Spawning.updateMap(Controller.size, Logic.npcList, Logic.weaponsList, Logic.medkitList);
                    display.setText("");
                    display.append("Poprawnie wczytano stan symulacji z pliku.\nWznawiam wybraną symulację.\n");
                    refreshGUIMap();
                    refreshTerrain();
                    GUI.panelRight.removeAll();
                    buttonClose.setVisible(false);
                    buttonClose.setVisible(false);

                    GUI.panelRight.removeAll();
                    GUI.panelRight.setLayout(new GridLayout(4,1));
                    buttonClose.setVisible(true);
                    GUI.panelRight.add(buttonTop);
                    GUI.panelRight.add(buttonMid);
                    GUI.panelRight.add(buttonBot);
                    GUI.panelRight.add(labelInfo);
                    GUI.panelRight.revalidate();
                    GUI.panelRight.repaint();
                    CSVGenerator.dataReseter();
                } catch (FileNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            } else {
                display.append("Anulowano proces wczytu pliku.\n");
            }
        });
        buttonClose.addActionListener(e -> {
            System.out.println("zamkniecie");
            System.exit(0);
        });
        frame.setVisible(true);
    }
    private static int[] getUserSimulationInput() {
        final int[] output = new int[2];
        final boolean[] gotDataFromUser = {false}; 
        //JFrame DataEntryFrame = new JFrame();
        // Creating a dialog for input
        JDialog inputDialog = new JDialog(Controller.SimulationFrame, "Wpisz dane początkowe symulacji", true);
        inputDialog.setSize(300, 200);
        inputDialog.setLayout(new GridLayout(3, 2));

        // Adding components to the dialog
        JLabel labelSize = new JLabel("<html>Rozmiar Planszy<br>Podaj liczbę z zakresu [10;40]</html>");
        JTextField textFieldSize = new JTextField();
        JLabel labelNPC = new JLabel("<html>Ilość NPC<br>Podaj liczbę z zakresu [2;50]</html>");
        JTextField textFieldNPC = new JTextField();
        JButton buttonRandom = new JButton("<html>Zacznij symulację<br>z losowymi danymi</html>");
        JButton buttonContinue = new JButton("<html>Zacznij symulację<br>z podanymi danymi</html>");

        inputDialog.add(labelSize);
        inputDialog.add(textFieldSize);
        inputDialog.add(labelNPC);
        inputDialog.add(textFieldNPC);
        inputDialog.add(buttonRandom); // Placeholder
        inputDialog.add(buttonContinue);

        // Action listener for the submit button
        buttonRandom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                output[0] = (int) ((Math.random() * (40 - 10)) + 10);
                output[1] = (int) ((Math.random() * (50 - 2)) + 2);
                gotDataFromUser[0] = true;
                inputDialog.dispose();
            }
        });

        // Action listener for the submit button
        buttonContinue.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    output[0] = Integer.parseInt(textFieldSize.getText());
                    output[1] = Integer.parseInt(textFieldNPC.getText());
                    //throw new Exception();
                    if(output[0]>=1 && output[0]<=40) {
                        if(output[1]>=2 && output[1]<=50) {
                            gotDataFromUser[0] = true;
                            inputDialog.dispose();
                        }
                    }
                    else{
                        throw new Exception("Exception message");
                    }
                    //gotDataFromUser[0] = true;
                    //inputDialog.dispose();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(inputDialog, "Wprowadzone dane nie są liczbami lub nie wprowadzono danych!", "Błąd", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(inputDialog, "Podano liczby spoza dozwolonego przedziału!", "Błąd", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Show the dialog and wait for user input
        inputDialog.setLocationRelativeTo(Controller.SimulationFrame); // Center the dialog
        inputDialog.setVisible(true);

        if (!gotDataFromUser[0]) {
            output[0] = -1;
            output[1] = -1;
        }

        return output;
    }

    public static void SimulationGUIEnd(Frame frame){
        GUI.display.append("Wygrywa NPC o ID: "+ Logic.npcList.getFirst().index+"\n");
        GUI.display.append("Zamknij okienko aby zakończyc symulację!\n");
        GUI.buttonTop.setVisible(false);
        GUI.buttonBot.setVisible(false);
        GUI.buttonMid.setVisible(false);
        GUI.panelRight.removeAll();
        GUI.panelRight.setLayout(new GridLayout(2,1));
        buttonClose.setVisible(true);
        GUI.panelRight.add(buttonClose);
        GUI.panelRight.add(labelInfo);
        GUI.panelRight.revalidate();
        GUI.panelRight.repaint();
        GUI.labelGrid.get(Logic.npcList.getFirst().posX).get(Logic.npcList.getFirst().posY).setBackground(Color.pink);
    }

    public static JPanel resetLabelGrid(JPanel panel) {
        JPanel newpanel = new JPanel(new GridLayout(Controller.size, Controller.size));
        for (int x = 0; x < labelGrid.size(); x++) {
            for (int y = 0; y < labelGrid.get(x).size(); y++) {
                labelGrid.get(x).get(y).setVisible(false);
                panel.remove(labelGrid.get(x).get(y));
            }
        }
        labelGrid.clear();
        for (int x = 0; x < Controller.size; x++) {
            labelGrid.add(new ArrayList<>());
        }
        for (int x = 0; x < Controller.size; x++) {
            for (int y = 0; y < Controller.size; y++) {
                JLabel label = new JLabel();
                label.setOpaque(true);
                label.setBorder(borderDefault);
                labelGrid.get(x).add(label);
                newpanel.add(label);
                //================================================
                //code responsible for changing the text in labelInfo when hovering mouse over a tile on the labelGrid
                label.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        //getting the X and Y coordinates of the label on the grid -> coordinates correspond to values being used in the rest of the simulation's logic
                        int labelPosX=-1;
                        int labelPosY=-1;
                        for (int i = 0; i < Controller.size; i++) {
                            for (int j = 0; j < Controller.size; j++) {
                                if (labelGrid.get(i).get(j) == label) {
                                    labelPosX = i;
                                    labelPosY = j;
                                    break;
                                }
                            }
                        }
                        labelInfoText.add("Puste pole planszy.");
                        for (int i = 0; i < Logic.npcList.size(); i++) {
                            if(Logic.npcList.get(i).posX==labelPosX && Logic.npcList.get(i).posY==labelPosY){
                                labelInfoText.clear();
                                labelInfoText.add("NPC ID: "+Logic.npcList.get(i).index);
                                labelInfoText.add("Max. HP: "+Logic.npcList.get(i).maxHP);
                                labelInfoText.add("Obecne HP: "+Logic.npcList.get(i).HP);
                                labelInfoText.add("Broń: "+Logic.npcList.get(i).weapon.name);
                                labelInfoText.add("Obrażenia: "+Logic.npcList.get(i).weapon.damage);
                                if(Logic.npcList.get(i).weapon.range == sqrt(2)) {
                                    labelInfoText.add("Zasięg ataku: "+(Logic.npcList.get(i).weapon.range / sqrt(2)));
                                }
                                else {
                                    labelInfoText.add("Zasięg ataku: " + Logic.npcList.get(i).weapon.range);
                                }
                                //labelInfoText.add("Teren: "+TerrainGenerator.terrainMap.get(Logic.npcList.get(i).posY).get(Logic.npcList.get(i).posX));
                                break;
                            }
                        }
                        for (int i = 0; i < Logic.weaponsList.size(); i++) {
                            if(Logic.weaponsList.get(i).posX==labelPosX && Logic.weaponsList.get(i).posY==labelPosY){
                                labelInfoText.clear();
                                labelInfoText.add("Broń: "+Logic.weaponsList.get(i).name);
                                labelInfoText.add("Obrażenia: "+Logic.weaponsList.get(i).damage);
                                if(Logic.weaponsList.get(i).range == sqrt(2)) {
                                    labelInfoText.add("Zasięg Broni: " + (Logic.weaponsList.get(i).range / sqrt(2)));
                                }
                                else {
                                    labelInfoText.add("Zasięg Broni: " + Logic.weaponsList.get(i).range);
                                }
                                break;
                            }
                        }
                        for (int i = 0; i < Logic.medkitList.size(); i++) {
                            if(Logic.medkitList.get(i)[0]==labelPosX && Logic.medkitList.get(i)[1]==labelPosY){
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
        newpanel.setVisible(true);
        Controller.SimulationFrame.remove(panel);
        Controller.SimulationFrame.add(newpanel);
        return newpanel;
    }

    public static String labelTextWrapper(List<String> text){
        String out = "<html>";
        for (int i = 0; i < text.size(); i++) {
            out = out + text.get(i) + "<br>";
        }
        return out + "</html>";
    }

    //this method DOES NOT refresh the terrain shown as colors under the items and npcs on the map
    public static void refreshGUIMap() {
        for (int y = 0; y < Controller.size; y++) {
            for (int x = 0; x < Controller.size; x++) {
                labelGrid.get(x).get(y).setBorder(borderDefault);
                labelGrid.get(x).get(y).setIcon(null);
            }
        }
        for (int i = 0; i < Logic.npcList.size(); i++) {
            labelGrid.get(Logic.npcList.get(i).posX).get(Logic.npcList.get(i).posY).setIcon(resizeImg(Logic.npcList.get(i).icon, (cellSize*25)/Controller.size, (cellSize*25)/Controller.size));
            labelGrid.get(Logic.npcList.get(i).posX).get(Logic.npcList.get(i).posY).setBorder(borderNPC);
        }
        for (int i = 0; i < Logic.weaponsList.size(); i++) {
            labelGrid.get(Logic.weaponsList.get(i).posX).get(Logic.weaponsList.get(i).posY).setIcon(resizeImg(Logic.weaponsList.get(i).icon, (cellSize*25)/Controller.size, (cellSize*25)/Controller.size));
            labelGrid.get(Logic.weaponsList.get(i).posX).get(Logic.weaponsList.get(i).posY).setBorder(borderWeapon);
        }
        for (int i = 0; i < Logic.medkitList.size(); i++) {
            labelGrid.get(Logic.medkitList.get(i)[0]).get(Logic.medkitList.get(i)[1]).setIcon(resizeImg(medkit, (cellSize*25)/Controller.size, (cellSize*25)/Controller.size));
            labelGrid.get(Logic.medkitList.get(i)[0]).get(Logic.medkitList.get(i)[1]).setBorder(borderMedkit);
        }
    }

    //teren w osobnej metodzie bo bardzo spowalnia program
    //a wywoływany musi być tylko raz
    public static void refreshTerrain(){
        for (int y = 0; y < Controller.size; y++) {
            for (int x = 0; x < Controller.size; x++) {
                //labelGrid.get(x).get(y).repaint();
                switch(TerrainGenerator.terrainMap.get(y).get(x)) {
                    case 0:
                        labelGrid.get(x).get(y).setBackground(Color.yellow.brighter());
                        break;
                    case 1:
                        labelGrid.get(x).get(y).setBackground(Color.green.darker());
                        break;
                    case 2:
                        labelGrid.get(x).get(y).setBackground(Color.green.darker().darker());
                        break;
                    case 3:
                        labelGrid.get(x).get(y).setBackground(Color.gray);
                        break;
                }
            }
        }
    }

    public static ImageIcon resizeImg(ImageIcon img, int width, int height) {
        Image newImg = img.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(newImg);
    }
}
