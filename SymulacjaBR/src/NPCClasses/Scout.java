package NPCClasses;
import WeaponClasses.*;

import javax.swing.*;

public class Scout extends NPC{
    public Scout(int index, int posX,int posY, int maxHP, int stamina, Weapon weapon, String symbol){
        super(index, posX, posY, maxHP, stamina, weapon, symbol);
        this.icon = new ImageIcon("scout.png");
    }
}
