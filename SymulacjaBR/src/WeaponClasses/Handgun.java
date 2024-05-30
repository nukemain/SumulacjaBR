package WeaponClasses;

import javax.swing.*;

public class Handgun extends Weapon {
    public Handgun(String name, int damage, int range, int quality, int posX, int posY){
        super(name, damage, range, quality, posX, posY);
        this.icon = new ImageIcon("handgun.png");
    }
}
