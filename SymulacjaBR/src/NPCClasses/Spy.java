package NPCClasses;
import WeaponClasses.*;

import javax.swing.*;

public class Spy extends NPC{
    public Spy(int index, int posX,int posY, int maxHP, int stamina, Weapon weapon, String symbol){
        super(index, posX, posY, maxHP, stamina, weapon, symbol);
        this.icon = new ImageIcon("spy.png");
    }
}
