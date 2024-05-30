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
    Weapon(String name, int damage, int range, int quality, int posX, int posY) {
        this.name = name;
        this.damage = damage;
        this.range = range*sqrt(2);
        this.quality = quality;
        this.posX = posX;
        this.posY = posY;
        this.icon = new ImageIcon("default.png");
    }
}
