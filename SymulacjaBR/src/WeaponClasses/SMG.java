package WeaponClasses;

import javax.swing.*;

public class SMG extends Weapon {
    public SMG(String name, int damage, int range, int quality, int posX, int posY){
        super(name, damage, range, quality, posX, posY);
        this.icon = new ImageIcon("smg.png");
    }
}
