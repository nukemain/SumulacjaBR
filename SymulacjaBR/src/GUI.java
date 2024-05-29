import javax.swing.*;
import java.awt.*;
/*
public class GUI {
    public GUI(){
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setTitle("Witam świecie");
        //frame.setResizable(false);
        ImageIcon logo = new ImageIcon("logo.png");
        ImageIcon logo2 = new ImageIcon("logo2.png");
        frame.setIconImage(logo.getImage());
        //frame.getContentPane().setBackground(Color.darkGray);

        JLabel label = new JLabel();
        label.setText("Witam w symulacji");
        label.setIcon(logo2);
        label.setHorizontalTextPosition(SwingConstants.CENTER);
        label.setVerticalTextPosition(SwingConstants.BOTTOM);
        label.setForeground(Color.black);
        label.setIconTextGap(20);



        frame.setVisible(true);
        frame.add(label);
    }
    public static void main(String[] args) {
        //System.out.println("hello world");
        new GUI();
    }
}*/
import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class GUI {
    private static final int SIZE = 10;
    private static JLabel[][] buttons = new JLabel[SIZE][SIZE];

    public static void main(String[] args) {
        // Create the frame
        JFrame frame = new JFrame("szachowncca");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);


        // Create a panel with a grid layout
        JPanel panel = new JPanel(new GridLayout(SIZE, SIZE));

        // Add buttons to the panel
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                JLabel button = new JLabel();
                // Set the button color to alternate between white and black
                button.setText("ABCDEF");

                if ((row + col) % 2 == 0) {
                    button.setForeground(Color.WHITE);
                } else {
                    button.setForeground(Color.BLACK);
                }
                buttons[row][col] = button;
                panel.add(button);
            }
        }

        // Add the panel to the frame
        frame.add(panel);
        frame.setVisible(true);
        int n=0;
        while (true){

            System.out.println("istnieję"); //pusta linijka potrzebna do formatowania
            panel.getComponent(n).setForeground(Color.GREEN);
            String[] test = new String[]{Arrays.toString(panel.getComponents())};
            for (int i = 0; i < test.length; i++) {
                System.out.println(test[i]+"\n");
            }


            n++;
            System.out.println("Press any key to continue...");
            try{System.in.read();}
            catch(Exception e){}
            changeButtonText(0, 0, "A1");
            changeButtonText(9, 9, "J10");
        }
    }
    public static void changeButtonText(int row, int col, String text) {
        if (row >= 0 && row < SIZE && col >= 0 && col < SIZE) {
            buttons[row][col].setText(text);
        } else {
            System.out.println("Invalid button position.");
        }
    }
}
