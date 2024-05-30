package WeaponClasses;

import javax.swing.*;

public class SniperRifle extends Weapon {
    public SniperRifle(String name, int damage, int range, int quality, int posX, int posY){
        super(name, damage, range, quality, posX, posY);
        this.icon = new ImageIcon("sniperrifle.png");
    }
}
