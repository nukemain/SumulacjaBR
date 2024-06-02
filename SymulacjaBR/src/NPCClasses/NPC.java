package NPCClasses;
import WeaponClasses.*;

import javax.swing.*;

public abstract class NPC {
    public int index;
    public String name;
    public int posX;
    public int posY;
    public int HP;
    public int maxHP;
    public int stamina;
    public Weapon weapon;
    public String symbol;
    public ImageIcon icon;
    NPC(int index, int posX,int posY, Weapon weapon){
        this.index = index;
        this.posX = posX;
        this.posY = posY;
        this.weapon = weapon;
        this.icon = new ImageIcon("default.png");
    }

    public void Ability() {
    }
}
