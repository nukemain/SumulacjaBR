package WeaponClasses;

import javax.swing.*;

public class Shotgun extends Weapon {
    public Shotgun(String name, int damage, int range, int quality, int posX, int posY){
        super(name, damage, range, quality, posX, posY);
        this.icon = new ImageIcon("shotgun.png");
    }
}
