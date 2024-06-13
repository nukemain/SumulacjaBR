package NPCClasses;
import WeaponClasses.*;

import javax.swing.*;

public abstract class NPC {
    /**
     * NPC's index
     */
    public int index;
    /**
     * NPC's name
     */
    public String name;
    /**
     * NPC's X position on the map
     */
    public int posX;
    /**
     * NPC's Y position on the map
     */
    public int posY;
    /**
     * NPC's HP, will never exceed maxHP
     */
    public int HP;
    /**
     * NPC's maximal HP
     */
    public int maxHP;
    /**
     * NPC's stamina
     */
    public int stamina;
    /**
     * NPC's weapon
     */
    public Weapon weapon;
    /**
     * NPC's symbol - used in string representation of the map
     */
    public String symbol;
    /**
     * NPC's icon - used in the GUI of the program
     */
    public ImageIcon icon;
    NPC(int index, int posX,int posY, Weapon weapon){
        this.index = index;
        this.posX = posX;
        this.posY = posY;
        this.weapon = weapon;
        this.icon = new ImageIcon(getClass().getResource("/default.png"));
    }

    public void Ability() {
        //certain classes have a special ability, coded inside this method
    }
}
