package WeaponClasses;

import javax.swing.*;

import static java.lang.Math.sqrt;

public abstract class Weapon {
    public String name;
    public int damage;
    public double range;
    public int quality;
    public int posX;
    public int posY;
    public ImageIcon icon;
    Weapon(int posX, int posY) {
        this.posX = posX;
        this.posY = posY;
        this.icon = new ImageIcon("default.png");
    }
    public int Attack(int targetHP){
        return 0;
    }
}
