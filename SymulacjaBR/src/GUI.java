import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import static java.lang.Math.sqrt;

public class GUI {
    static private final int cellSize = 30;
    static private JLabel labelInfo = new JLabel();
    static List<String> labelInfoText = new ArrayList<>();
    static List<List<JLabel>> labelGrid = new ArrayList<>();
    static JPanel mainPanel = new JPanel();

    //borders used for accents around NPC, weapon and medkit icons
    static private Border borderDefault = BorderFactory.createLineBorder(Color.black, 1);
    static private Border borderNPC = BorderFactory.createLineBorder(Color.red, 1);
    static private Border borderWeapon = BorderFactory.createLineBorder(Color.orange, 1);
    static private Border borderMedkit = BorderFactory.createLineBorder(Color.green, 1);


    static private JButton buttonTop = new JButton("Następna tura");
    static private JButton buttonMid = new JButton("Zapisz stan planszy");
    static private JButton buttonBot = new JButton("Wczytaj stan planszy");
    static private JButton buttonClose = new JButton("Zamknij program");
    static private JButton buttonNewSim = new JButton("Zacznij nową symulację");
    static private JPanel panelRight = new JPanel(new GridLayout(3, 1));

    //NPC's and weapons already have a defined icon,but medkits do not, so we create a new ImageIcon here
    static private ImageIcon medkit = new ImageIcon("images/medpack.png");
    static JTextArea display = new JTextArea(16, 58);


    //code responsible for the window showing the simulation
    public static void SimulationGUI(JFrame frame) {

        //code for closing the main window
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        // Add window listener to print message on window closing
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int returnValue=-1;
                if(Logic.npcList.size() <= 1) {  //if there is no simulation currently running, close the program with no alerts
                    System.exit(0);
                }else{//if there is a simulation currently running, present 3 options to the user..
                    String[] buttons = {"Zamknij","Zapisz symulację i zamknij", "Anuluj"};
                    returnValue = JOptionPane.showOptionDialog(null, "Zamknięcie programu bezpowrotnie zakończy obecną symulację!", "Uwaga! - Symulacja w toku!",
                            JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, buttons, buttons[0]);
                }

                switch (returnValue) {
                    case 0://...close anyway...
                        System.exit(0);
                        break;
                    case 1://...save the simulation and then close...
                        try {
                            FileSaver.fileSaver(Logic.size);
                            System.exit(0);
                        } catch (FileNotFoundException ex) { //handle exception
                            JOptionPane.showMessageDialog(null, "Wystąpił błąd przy zapisywaniu - czy wybrano poprawny plik do zapisu?", "Błąd", JOptionPane.ERROR_MESSAGE);
                        }
                        break;
                    case 2://...cancel the action
                        //do nothing
                        break;
                    default:
                        //do nothing
                        break;
                }
            }
        });
        //set Size, title, and icon of the main window
        frame.setSize(200 + cellSize * 25, 150 + cellSize * 25);
        frame.setTitle("Symulacja battle royale");
        frame.setResizable(false);
        ImageIcon logo = new ImageIcon("images/logo.png");
        frame.setIconImage(logo.getImage());

        //create the main panel showing the simulation's status
        mainPanel = new JPanel(new GridLayout(Logic.size, Logic.size));
        mainPanel = resetLabelGrid(mainPanel);
        mainPanel.setBorder(borderDefault);

        //prepare the panel on the right containing all the right side buttons and a label
        panelRight.removeAll();
        //add items to panel
        panelRight.add(buttonNewSim);
        panelRight.add(buttonBot);
        panelRight.add(buttonClose);

        //prepare the right label, but don't show it yet
        labelInfo.setBorder(borderDefault);
        labelInfo.setText("<html>Najedź na pole planszy<br>aby zobaczyć związane z nim dane!</html>");


        //adding the scrollPane at the bottom of the window, and adding a text display to it.
        JScrollPane bottomScrollPane = new JScrollPane(display);
        bottomScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS); //Vertical scrollbar settings
        bottomScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);//Horizontal scrollbar settings
        bottomScrollPane.setPreferredSize(new Dimension(frame.getWidth(), 150)); //Size of the scrollpane
        display.setEditable(false);
        display.append("Wciśnij i przytrzymaj przycisk \"Nastepna tura\" aby aktywować automatyczne wykonywanie ruchów!\n");

        //add ScrollPane, panelRight  and mainPanel to the main frame
        frame.add(bottomScrollPane, BorderLayout.PAGE_END);
        frame.add(mainPanel, BorderLayout.CENTER);
        frame.add(panelRight, BorderLayout.EAST);
        //show the right panel
        panelRight.setVisible(true);

        //add a ActionListener to the "new simulation" button
        buttonNewSim.addActionListener(e -> {
            //same as when closing the main window, we check if there is a simulation already running, and act accordingly
            int returnValue=-1;
            if(Logic.npcList.size() <= 1) { // no simulation running, proceed with no alerts
                returnValue = JOptionPane.OK_OPTION;
            }else{//simulation running, ask user if he is sure about continuing
                String[] buttons = {"Kontynuuj", "Anuluj"};
                returnValue = JOptionPane.showOptionDialog(null, "To działanie bezpowrotnie zakończy obecną symulację!", "Uwaga! - Symulacja w toku!",
                        JOptionPane.OK_CANCEL_OPTION, 1, null, buttons, buttons[0]);
            }

            if (returnValue == JOptionPane.OK_OPTION) { //if user agreed to continue


                int[] newParams = getUserSimulationInput(); //get new simulation params from user
                Logic.size = newParams[0];
                Logic.NPCcount = newParams[1];
                mainPanel = resetLabelGrid(mainPanel); //reset the map
                mainPanel.setVisible(true);

                //change content visible on the right side panel
                GUI.panelRight.removeAll();
                GUI.panelRight.setLayout(new GridLayout(5, 1));
                buttonClose.setVisible(true);
                //add four buttons
                GUI.panelRight.add(buttonTop);
                GUI.panelRight.add(buttonMid);
                GUI.panelRight.add(buttonBot);
                GUI.panelRight.add(buttonNewSim);
                //add label with grid information
                GUI.panelRight.add(labelInfo);
                GUI.panelRight.revalidate();
                GUI.panelRight.repaint();


                //reset the simulation - clear all Lists, deleting the existing simulation
                Logic.map.clear();
                Logic.npcList.clear();
                Logic.weaponsList.clear();
                Logic.medkitList.clear();
                TerrainGenerator.terrainMap.clear();

                //create a new simulation
                Logic.map = Spawning.createMap(Logic.size);//create a clear map
                TerrainGenerator.terrainGenerator(Logic.size);//generate terrain
                Spawning.spawnNPCs(Logic.size, Logic.NPCcount, Logic.map, Logic.npcList);//spwan the NPC's
                Spawning.spawnWeapons(Logic.map, Logic.size, Logic.NPCcount, Logic.weaponsList);//Spawning weapons on the map
                Spawning.spawnMedkits(Logic.map, Logic.size, Logic.NPCcount, Logic.medkitList);//spawning medkits

                Spawning.updateMap(Logic.size, Logic.npcList, Logic.weaponsList, Logic.medkitList);//update the map
                refreshGUIMap();//refresh map shown in GUI
                refreshTerrain();//refresh terrain shown in GUI
                CSVGenerator.dataReseter();//reset written data
                display.setText("Wciśnij i przytrzymaj przycisk \"Nastepna tura\" aby aktywować automatyczne wykonywanie ruchów!\n");//reset the bottom text display

                //code required for unpausing the main simulation loop
                synchronized (Logic.lock) {
                    Logic.buttonPressed = true;
                    Logic.lock.notify();
                }
            }
        });
        frame.setVisible(true);

        //add a ActionListener to the "next turn" button
        buttonTop.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                //while button is being pressed, notify lock pausing the execution of the main simulation loop
                Logic.buttonHeld = true;
                new Thread(new Runnable() {
                    public void run() {
                        while (Logic.buttonHeld) {
                            synchronized (Logic.lock) {
                                Logic.buttonPressed = true;
                                Logic.lock.notify();
                            }
                            try {
                                Thread.sleep(500);//delay between turns when button is being held, in [ms]
                            } catch (InterruptedException ignored) {
                            }
                        }
                    }
                }).start();// start the thread
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                Logic.buttonHeld = false; //when released, set buttonHeld value to false
            }
        });

        //add a ActionListener to the "save simulation" button
        buttonMid.addActionListener(e -> {
            try {
                FileSaver.fileSaver(Logic.size);//save file
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });

        buttonBot.addActionListener(e -> {
            int returnValue=-1;
            if(Logic.npcList.size() <= 1) { // no simulation running, proceed with no alerts
                returnValue = JOptionPane.OK_OPTION;
            }else{//simulation running, ask user if he is sure about continuing
                String[] buttons = {"Kontynuuj", "Anuluj"};
                returnValue = JOptionPane.showOptionDialog(null, "Wczytanie pliku symulacji zakończy obecną symulację!", "Uwaga!",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, buttons, buttons[0]);
            }

            if (returnValue == JOptionPane.OK_OPTION) {
                try {
                    //read simulation state from file
                    FileReader.fileReader();
                    Spawning.updateMap(Logic.size, Logic.npcList, Logic.weaponsList, Logic.medkitList); //update the map with new data
                    display.setText("Poprawnie wczytano stan symulacji z pliku.\nWznawiam wybraną symulację.\n"); //update the bottom text display
                    //refresh map-related gui elements
                    refreshGUIMap();
                    refreshTerrain();
                    CSVGenerator.dataReseter();//reset data collection process

                    //change elements visible in the right pannel
                    GUI.panelRight.removeAll();
                    buttonClose.setVisible(false);
                    GUI.panelRight.setLayout(new GridLayout(4,1));//change layout to fit more items
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
        //add a ActionListener to the "Close program" button
        buttonClose.addActionListener(e -> {
            System.exit(0);
        });
        frame.setVisible(true);
    }

    //method used for getting user input in form of a dialog box
    private static int[] getUserSimulationInput() {
        final int[] output = new int[2]; //output is array consisting of two integers
        final boolean[] gotDataFromUser = {false};

        //creating a dialog for input
        JDialog inputDialog = new JDialog(Logic.SimulationFrame, "Wpisz dane początkowe symulacji", true);
        inputDialog.setSize(300, 200);
        inputDialog.setLayout(new GridLayout(3, 2));

        //creating components used in dialog
        JLabel labelSize = new JLabel("<html>Rozmiar Planszy<br>Podaj liczbę z zakresu [10;40]</html>");
        JTextField textFieldSize = new JTextField();
        JLabel labelNPC = new JLabel("<html>Ilość NPC<br>Podaj liczbę z zakresu [2;50]</html>");
        JTextField textFieldNPC = new JTextField();
        JButton buttonRandom = new JButton("<html>Zacznij symulację<br>z losowymi danymi</html>");
        JButton buttonContinue = new JButton("<html>Zacznij symulację<br>z podanymi danymi</html>");

        //adding components to the dialog
        inputDialog.add(labelSize);
        inputDialog.add(textFieldSize);
        inputDialog.add(labelNPC);
        inputDialog.add(textFieldNPC);
        inputDialog.add(buttonRandom);
        inputDialog.add(buttonContinue);

        //adding action listener to the "random params" button
        buttonRandom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //two random integers from set range
                output[0] = (int) ((Math.random() * (40 - 10)) + 10);
                output[1] = (int) ((Math.random() * (50 - 2)) + 2);
                gotDataFromUser[0] = true;
                inputDialog.dispose();
            }
        });

        //adding action listener to the "use set params" button
        buttonContinue.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    output[0] = Integer.parseInt(textFieldSize.getText());
                    output[1] = Integer.parseInt(textFieldNPC.getText());
                    if(output[0]>=1 && output[0]<=40) {
                        if(output[1]>=2 && output[1]<=50) {
                            gotDataFromUser[0] = true;
                            inputDialog.dispose();
                        }
                    }
                    else{
                        //throw exception if user's numbers are not in set range
                        throw new Exception();
                    }
                } catch (NumberFormatException ex) {//show alert box with information
                    JOptionPane.showMessageDialog(inputDialog, "Wprowadzone dane nie są liczbami lub nie wprowadzono danych!", "Błąd", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {//show alert box with information
                    JOptionPane.showMessageDialog(inputDialog, "Podano liczby spoza dozwolonego przedziału!", "Błąd", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        //show the dialog, then wait for user input
        inputDialog.setLocationRelativeTo(Logic.SimulationFrame); //center the dialog box in the middle of simulation's window
        inputDialog.setVisible(true);

        if (!gotDataFromUser[0]) {
            output[0] = -1;
            output[1] = -1;
        }

        return output;
    }

    //method called when the simulation ends
    public static void SimulationGUIEnd(){
        //display winner data
        GUI.display.append("Wygrywa NPC  "+ Logic.npcList.get(0).name+"\n"); //using get(0) instead of getFirst() due to technical issue
        GUI.display.append("Zamknij okienko aby zakończyc symulację!\n");

        //change elements visible in the right panel
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

        //highlight the winning NPC with a pink background
        GUI.labelGrid.get(Logic.npcList.get(0).posX).get(Logic.npcList.get(0).posY).setBackground(Color.pink);
    }

    //method used to reset the grid of labels used to display the simulation's map
    public static JPanel resetLabelGrid(JPanel panel) {
        //Simulation's map works as a grid of Label objects with an ImageIcon used as a way to display NPCs, weapons and medkits
        //while the background color is used to convey information to the user about the terrain at set map coordinates

        JPanel newpanel = new JPanel(new GridLayout(Logic.size, Logic.size));
        //remove existing labels from the panel on which they are displayed
        for (int x = 0; x < labelGrid.size(); x++) {
            for (int y = 0; y < labelGrid.get(x).size(); y++) {
                labelGrid.get(x).get(y).setVisible(false);
                panel.remove(labelGrid.get(x).get(y));
            }
        }
        labelGrid.clear();//clear the list of label objects

        //add
        for (int x = 0; x < Logic.size; x++) {
            labelGrid.add(new ArrayList<>());
            for (int y = 0; y < Logic.size; y++) {
                JLabel label = new JLabel();
                label.setOpaque(true);
                label.setBorder(borderDefault);
                labelGrid.get(x).add(label);
                newpanel.add(label);

                //each label added to the labelGrid gets a MouseListener added
                //code responsible for changing the text in labelInfo when hovering mouse over a tile on the labelGrid
                label.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        //getting the X and Y coordinates of the label on the grid -> coordinates correspond to values being used in the rest of the simulation's logic
                        int labelPosX=-1;
                        int labelPosY=-1;
                        for (int i = 0; i < Logic.size; i++) {
                            for (int j = 0; j < Logic.size; j++) {
                                if (labelGrid.get(i).get(j) == label) {
                                    labelPosX = i;
                                    labelPosY = j;
                                    break;
                                }
                            }
                        }
                        //add text to labelInfoText List, later use labelTextWrapper() on it to set labelInfo text
                        labelInfoText.add("Puste pole planszy.");
                        for (int i = 0; i < Logic.npcList.size(); i++) {
                            //add NPC specific text to labelInfoText List
                            if(Logic.npcList.get(i).posX==labelPosX && Logic.npcList.get(i).posY==labelPosY){
                                labelInfoText.clear();
                                labelInfoText.add("Nazwa: "+Logic.npcList.get(i).name);
                                labelInfoText.add("HP: "+Logic.npcList.get(i).HP+"(MAX:"+Logic.npcList.get(i).maxHP+")");
                                labelInfoText.add("Broń: "+Logic.npcList.get(i).weapon.name+"(DMG:"+Logic.npcList.get(i).weapon.damage+")");
                                if(Logic.npcList.get(i).weapon.range%sqrt(2)==0) {
                                    labelInfoText.add("Zasięg ataku: "+(Logic.npcList.get(i).weapon.range / sqrt(2)));
                                }
                                else {
                                    labelInfoText.add("Zasięg ataku: " + Logic.npcList.get(i).weapon.range);
                                }
                                if(Logic.npcList.get(i).symbol == "μ")//medic
                                {
                                    labelInfoText.add("HP +=4 co turę");
                                }
                                if(Logic.npcList.get(i).symbol == "Λ")//scout
                                {
                                    labelInfoText.add("Więcej staminy");
                                }
                                if(Logic.npcList.get(i).symbol == "Θ")//sniper
                                {
                                    labelInfoText.add("+=1 do zasięgu");
                                    labelInfoText.add("(poza nożem)");
                                }
                                if(Logic.npcList.get(i).symbol == "Σ")//solidier
                                {
                                    labelInfoText.add("DMG 1.2x jeśli");
                                    labelInfoText.add("dystans do celu=1");
                                }
                                if(Logic.npcList.get(i).symbol == "Ω")//spy
                                {
                                    labelInfoText.add("30% szansy na unik");
                                }
                                break;
                            }
                        }
                        for (int i = 0; i < Logic.weaponsList.size(); i++) {
                            //add weapon specific text to labelInfoText List
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
                                if(Objects.equals(Logic.weaponsList.get(i).name, "Knife")){
                                    labelInfoText.add("5% sznasy na oneshota");
                                }
                                if(Objects.equals(Logic.weaponsList.get(i).name, "Rifle")){
                                    labelInfoText.add("30% szansy na +5 DMG");
                                }
                                if(Objects.equals(Logic.weaponsList.get(i).name, "SniperRifle")){
                                    labelInfoText.add("50% szansy na 1.5x DMG");
                                }
                                if(Objects.equals(Logic.weaponsList.get(i).name, "Shotgun")){
                                    labelInfoText.add("8 strzałów");
                                    labelInfoText.add("50% szansy na trafienie");
                                }

                                break;
                            }
                        }
                        for (int i = 0; i < Logic.medkitList.size(); i++) {
                            //add medkit specific text to labelInfoText List
                            if(Logic.medkitList.get(i)[0]==labelPosX && Logic.medkitList.get(i)[1]==labelPosY){
                                labelInfoText.clear();
                                labelInfoText.add("Medkit: +30HP");
                                break;
                            }
                        }

                        //add terrain specific text to labelInfoText List
                        labelInfoText.add("==========");
                        switch(TerrainGenerator.terrainMap.get(labelPosY).get(labelPosX)) {
                            case 0://desert
                                labelInfoText.add("Pustynia (Stamina -=1)");
                                break;
                            case 1://field
                                labelInfoText.add("Polana (Brak zmian)");
                                break;
                            case 2://forest
                                labelInfoText.add("Las (Zasięg =1)");
                                break;
                            case 3://mountains
                                labelInfoText.add("Góry (Zasięg +=1)");
                                break;
                            case 4: //zone
                                labelInfoText.add("Strefa");
                                labelInfoText.add("HP -=10 na ture");
                                break;
                        }
                        //add coordinates of cutrrent tile to labelInfoText List
                        labelInfoText.add("("+labelPosX+","+labelPosY+")");

                        //set labelInfo text to new value
                        labelInfo.setText(labelTextWrapper(labelInfoText));
                    }

                    @Override
                    public void mouseExited(MouseEvent e) { //reset text when mouse leaves the label's area
                        labelInfoText.clear();
                        labelInfo.setText("<html>Nie zaznaczono<br>pola planszy.</html>"); //no need to call labelTextWrapper() for just one sentence
                    }
                });
            }
        }
        newpanel.setVisible(true);
        Logic.SimulationFrame.remove(panel);
        Logic.SimulationFrame.add(newpanel);
        return newpanel;
    }

    //method used to format text shown inside labelInfo
    private static String labelTextWrapper(List<String> text){
        //labels do not have any built in text wrapping, but they use html tags in their text formatting,
        //so we insert a "<br>" between lines to break up the text

        String out = "<html>";
        for (int i = 0; i < text.size(); i++) {
            out = out + text.get(i) + "<br>";
        }
        return out + "</html>";
    }

    //method used to refresh NPCs, medkits and weapons shown in the GUI
    //this method DOES NOT refresh the terrain shown as colors under the items and NPCs on the map
    public static void refreshGUIMap() {
        for (int y = 0; y < Logic.size; y++) {
            for (int x = 0; x < Logic.size; x++) {
                labelGrid.get(x).get(y).setBorder(borderDefault);
                labelGrid.get(x).get(y).setIcon(null);
            }
        }
        for (int i = 0; i < Logic.npcList.size(); i++) {
            labelGrid.get(Logic.npcList.get(i).posX).get(Logic.npcList.get(i).posY).setIcon(resizeImg(Logic.npcList.get(i).icon, (cellSize*25)/Logic.size, (cellSize*25)/Logic.size));
            labelGrid.get(Logic.npcList.get(i).posX).get(Logic.npcList.get(i).posY).setBorder(borderNPC);
        }
        for (int i = 0; i < Logic.weaponsList.size(); i++) {
            labelGrid.get(Logic.weaponsList.get(i).posX).get(Logic.weaponsList.get(i).posY).setIcon(resizeImg(Logic.weaponsList.get(i).icon, (cellSize*25)/Logic.size, (cellSize*25)/Logic.size));
            labelGrid.get(Logic.weaponsList.get(i).posX).get(Logic.weaponsList.get(i).posY).setBorder(borderWeapon);
        }
        for (int i = 0; i < Logic.medkitList.size(); i++) {
            labelGrid.get(Logic.medkitList.get(i)[0]).get(Logic.medkitList.get(i)[1]).setIcon(resizeImg(medkit, (cellSize*25)/Logic.size, (cellSize*25)/Logic.size));
            labelGrid.get(Logic.medkitList.get(i)[0]).get(Logic.medkitList.get(i)[1]).setBorder(borderMedkit);
        }
    }

    //method used to refresh terrain shown in the GUI
    //this method DOES NOT refresh NPCs, medkits and weapons shown on the map
    public static void refreshTerrain(){
        //iterate through the map and set each label's background color
        for (int y = 0; y < Logic.size; y++) {
            for (int x = 0; x < Logic.size; x++) {
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
                    case 4:
                        labelGrid.get(x).get(y).setBackground(new Color(92, 1, 117)); //no set value for purple, so we create a new one
                }
            }
        }
    }

    //method used for ImageIcon resizing
    //ImageIcons cannot be resized, but images can,
    //so we convert an ImageIcon into an Image,resize it, and convert back
    public static ImageIcon resizeImg(ImageIcon img, int width, int height) {
        Image newImg = img.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(newImg);
    }
}
