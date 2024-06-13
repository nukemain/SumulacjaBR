package WeaponClasses;
import javax.swing.*;
import static java.lang.Math.sqrt;

public abstract class Weapon {
    /**
     * Weapon's name
     */
    public String name;
    /**
     * Weapon's damage
     */
    public int damage;
    /**
     * Weapon's range
     */
    public double range;
    /**
     * Weapon's quality
     */
    public int quality;
    /**
     * Weapon's X position on the map
     */
    public int posX;
    /**
     * Weapon's Y position on the map
     */
    public int posY;
    /**
     * Weapon's icon displayed in the GUI
     */

    public ImageIcon icon;
    Weapon(int posX, int posY) {
        this.posX = posX;
        this.posY = posY;
        this.icon = new ImageIcon(getClass().getResource("/default.png"));
    }
    public int Attack(int targetHP){
        return 0;
    }
}
