package WeaponClasses;

import javax.swing.*;

public class Rifle extends Weapon {
    public Rifle(String name, int damage, int range, int quality, int posX, int posY){
        super(name, damage, range, quality, posX, posY);
        this.icon = new ImageIcon("rifle.png");
    }
}
