import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.sqrt;

public class GUI {
    static int cellSize = 30;
    static JLabel labelInfo = new JLabel();
    static List<String> labelInfoText = new ArrayList<>();
    static List<List<JLabel>> labelGrid = new ArrayList<>();
    static JPanel mainPanel = new JPanel();
    static Border borderDefault = BorderFactory.createLineBorder(Color.black, 1);
    static Border borderNPC = BorderFactory.createLineBorder(Color.red, 1);
    static Border borderWeapon = BorderFactory.createLineBorder(Color.orange, 1);
    static Border borderMedkit = BorderFactory.createLineBorder(Color.green, 1);
    static JButton buttonTop = new JButton("Następna tura");
    static JButton buttonMid = new JButton("Zapisz stan planszy");
    static JButton buttonBot = new JButton("Wczytaj stan planszy");
    static JButton buttonClose = new JButton("Zamknij program");
    static JButton buttonNewSim = new JButton("Zacznij nową symulację");
    static JPanel panelRight = new JPanel(new GridLayout(3, 1));

    static ImageIcon medkit = new ImageIcon("medpack.png");
    static JTextArea display = new JTextArea(16, 58);

    static JFrame DataEntryFrame = new JFrame("User Input Window");



    //all the code responsible for the window showing the simulation
    public static void SimulationGUI(JFrame frame) {


        buttonClose.setVisible(false);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // Add window listener to print message on window closing
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("Zamknięto okno symulacji!");
                System.exit(0);
            }
        });
        frame.setSize(200 + cellSize * Controller.size, 150 + cellSize * Controller.size);
        frame.setTitle("Symulacja battle royale");
        //frame.setResizable(false);
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

            //DataEntryFrame.setVisible(true);
            //int[] newParams = getUserSimulationInput(DataEntryFrame);
            //Controller.size = newParams[0];
            //Controller.NPCcount = newParams[1];
            mainPanel.setVisible(true);
            //change buttons visible on the right side
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

            //reset simulation
            GUI.panelRight.revalidate();
            GUI.panelRight.repaint();
            Logic.map.clear();
            Logic.npcList.clear();
            Logic.weaponsList.clear();
            Logic.medkitList.clear();

            //create a new simulation
            Logic.map = Spawning.createMap(Controller.size);
            TerrainGenerator.terrainGenerator(Controller.size);
            Spawning.spawnNPCs(Controller.size, Controller.NPCcount, Logic.map, Logic.npcList);//Logic required for spawning NPCClasses.NPC's
            Spawning.spawnWeapons(Logic.map,Controller.size,Controller.NPCcount, Logic.weaponsList);//Spawning weapons on the map
            Spawning.spawnMedkits(Logic.map,Controller.size, Controller.NPCcount, Logic.medkitList);
            //refresh map and gui
            Spawning.updateMap(Controller.size, Logic.npcList, Logic.weaponsList, Logic.medkitList);
            refreshGUIMap();
            synchronized (Logic.lock) {
                Logic.buttonPressed = true;
                Logic.lock.notify();
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
            int returnValue = JOptionPane.showOptionDialog(null, "Wczytanie pliku symulacji zakończy obecna symulację!", "Uwaga!",
                    JOptionPane.OK_CANCEL_OPTION, 1, null, buttons, buttons[0]);
            if (returnValue == JOptionPane.OK_OPTION) {
                try {
                    FileReader.fileReader();
                    Spawning.updateMap(Controller.size, Logic.npcList, Logic.weaponsList, Logic.medkitList);
                    display.setText("");
                    display.append("Poprawnie wczytano stan symulacji z pliku.\nWznawiam wybraną symulację.\n");
                    refreshGUIMap();
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

    private static int[] getUserSimulationInput(Frame DataEntryFrame) { //todo: this fuckin' thing
        int[] output = new int[2];

        //DataEntryFrame.setLocation(JFrame.EXIT_ON_CLOSE);
        DataEntryFrame.setSize(400, 200);
        DataEntryFrame.setLayout(new GridLayout(4, 2));


        JLabel labelSize = new JLabel("Rozmiar:");
        JTextField textFieldSize = new JTextField();
        JLabel labelNPC = new JLabel("Ilość NPC");
        JTextField textFieldNPC = new JTextField();

        // Create the button to submit inputs
        JButton submitButton = new JButton("Kontyynuj");

        DataEntryFrame.add(labelSize);
        DataEntryFrame.add(textFieldSize);
        DataEntryFrame.add(labelNPC);
        DataEntryFrame.add(textFieldNPC);
        DataEntryFrame.add(new JLabel()); // Placeholder for layout
        DataEntryFrame.add(submitButton);

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get user inputs
                //String input1 = textFieldSize.getText();
                try{
                    output[0] = Integer.parseInt(textFieldSize.getText());
                }catch (NumberFormatException ex) {
                    System.out.println("to nie liczba kretynie");
                    output[0] = -1;
                }
                try{
                    output[1] = Integer.parseInt(textFieldSize.getText());
                }catch (NumberFormatException ex) {
                    System.out.println("to nie liczba kretynie");
                    output[1] = -1;
                }

            }
        });
        //output[0] = 30; //size
        //output[1] = 10; //npcamount
        //DataEntryFrame.setVisible(false);
        if(output[0]<20||output[0]>50){
            System.out.println("niepoprawna liczba");
        }else{
            DataEntryFrame.setVisible(false);
            return output;
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

    public static void refreshGUIMap() {
        for (int y = 0; y < Controller.size; y++) {
            for (int x = 0; x < Controller.size; x++) {
                labelGrid.get(x).get(y).setIcon(null);
                switch(TerrainGenerator.terrainMap.get(y).get(x)) {
                    case 0:
                        labelGrid.get(x).get(y).setBackground(Color.green.darker());
                        break;
                    case 1:
                        labelGrid.get(x).get(y).setBackground(Color.yellow.brighter());
                        break;
                    case 2:
                        labelGrid.get(x).get(y).setBackground(Color.green.darker().darker());
                        break;
                    case 3:
                        labelGrid.get(x).get(y).setBackground(Color.gray);
                        break;
                }
                labelGrid.get(x).get(y).setBorder(borderDefault);
            }
        }

        for (int i = 0; i < Logic.npcList.size(); i++) {
            labelGrid.get(Logic.npcList.get(i).posX).get(Logic.npcList.get(i).posY).setIcon(resizeImg(Logic.npcList.get(i).icon, cellSize, cellSize));
            labelGrid.get(Logic.npcList.get(i).posX).get(Logic.npcList.get(i).posY).setBorder(borderNPC);
        }
        for (int i = 0; i < Logic.weaponsList.size(); i++) {
            labelGrid.get(Logic.weaponsList.get(i).posX).get(Logic.weaponsList.get(i).posY).setIcon(resizeImg(Logic.weaponsList.get(i).icon, cellSize, cellSize));
            labelGrid.get(Logic.weaponsList.get(i).posX).get(Logic.weaponsList.get(i).posY).setBorder(borderWeapon);
        }
        for (int i = 0; i < Logic.medkitList.size(); i++) {
            labelGrid.get(Logic.medkitList.get(i)[0]).get(Logic.medkitList.get(i)[1]).setIcon(resizeImg(medkit, cellSize, cellSize));
            labelGrid.get(Logic.medkitList.get(i)[0]).get(Logic.medkitList.get(i)[1]).setBorder(borderMedkit);
        }
    }

    public static ImageIcon resizeImg(ImageIcon img, int width, int height) {
        Image newImg = img.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(newImg);
    }
}
