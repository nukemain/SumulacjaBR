import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.sqrt;

public class GUI {
    static int cellSize = 30;
    static JLabel labelInfo = new JLabel();
    static List<String> labelInfoText = new ArrayList<>();
    //static JLabel[][] labelGrid = new JLabel[Controller.size][Controller.size];
    static List<List<JLabel>> labelGrid = new ArrayList<>();
    static JPanel mainPanel = new JPanel();
    static Border borderDefault = BorderFactory.createLineBorder(Color.black, 1);
    static Border borderNPC = BorderFactory.createLineBorder(Color.red, 1);
    static Border borderWeapon = BorderFactory.createLineBorder(Color.yellow, 1);
    static Border borderMedkit = BorderFactory.createLineBorder(Color.green , 1);
    static JButton buttonTop = new JButton("Następna tura");
    static JButton buttonMid = new JButton("Zapisz stan planszy");
    static JButton buttonBot = new JButton("Wczytaj stan planszy");

    static ImageIcon medkit = new ImageIcon("medpack.png");
    static JTextArea display = new JTextArea(16, 58);

    public static void SimulationGUI(JFrame frame) {

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(200 + cellSize * Controller.size, 150 + cellSize * Controller.size);
        frame.setTitle("Symulacja battle royale");
        //frame.setResizable(false);
        ImageIcon logo = new ImageIcon("logo.png");
        mainPanel = new JPanel(new GridLayout(Controller.size, Controller.size));
        frame.setIconImage(logo.getImage());

        mainPanel = resetLabelGrid(mainPanel);

        JPanel panelRight = new JPanel(new GridLayout(4, 1));
        panelRight.add(buttonTop);
        panelRight.add(buttonMid);
        buttonTop.setBorder(borderDefault);
        buttonMid.setBorder(borderDefault);
        panelRight.add(buttonBot);
        panelRight.add(labelInfo);
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
                } catch (FileNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            } else {
                display.append("Anulowano proces zapisu pliku.\n");
            }
        });
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
                                labelInfoText.add("Zadawane obrażenia: "+Logic.npcList.get(i).weapon.damage);
                                labelInfoText.add("Zasięg ataku: "+(Logic.npcList.get(i).weapon.range/sqrt(2)));
                                break;
                            }
                        }
                        for (int i = 0; i < Logic.weaponsList.size(); i++) {
                            if(Logic.weaponsList.get(i).posX==labelPosX && Logic.weaponsList.get(i).posY==labelPosY){
                                labelInfoText.clear();
                                labelInfoText.add("Broń: "+Logic.weaponsList.get(i).name);
                                labelInfoText.add("Zadawane obrażenia: "+Logic.weaponsList.get(i).damage);
                                labelInfoText.add("Zasięg Broni: "+(Logic.weaponsList.get(i).range/sqrt(2)));
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
        Logic.frame.remove(panel);
        Logic.frame.add(newpanel);
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
                labelGrid.get(x).get(y).setBackground(Color.gray);
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
