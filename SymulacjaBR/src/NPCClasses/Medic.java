package NPCClasses;
import WeaponClasses.*;

import javax.swing.*;

public class Medic extends NPC{
    public Medic(int index, int posX,int posY, int maxHP, int stamina, Weapon weapon, String symbol){
        super(index, posX, posY, maxHP, stamina, weapon, symbol);
        this.icon = new ImageIcon("medic.png");
    }
}
