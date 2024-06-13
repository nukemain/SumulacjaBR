package NPCClasses;
import WeaponClasses.*;

import javax.swing.*;

public class Scout extends NPC{
    String[] names = {"Nibbles", "Crawly", "Rascal", "Sneaky", "Tunneler", "Slugger", "Writher", "Digger", "Inchy", "Mudworm"};
    public Scout(int index, int posX,int posY,Weapon weapon){
        super(index, posX, posY, weapon);
        this.maxHP = 90;
        this.HP = this.maxHP;
        this.stamina = 3;
        this.symbol = "R";
        this.name = names[(int) (Math.random() * (10))];
        this.icon = new ImageIcon(getClass().getResource("/scout.png"));
    }
}
