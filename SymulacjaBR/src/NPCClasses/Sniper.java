package NPCClasses;
import WeaponClasses.*;

import javax.swing.*;

public class Sniper extends NPC{
    public Sniper(int index, int posX,int posY, int maxHP, int stamina, Weapon weapon, String symbol){
        super(index, posX, posY, maxHP, stamina, weapon, symbol);
        this.icon = new ImageIcon("sniper.png");
    }
}
