package NPCClasses;
import WeaponClasses.*;

import javax.swing.*;

public abstract class NPC {
    public int index;
    public int posX;
    public int posY;
    public int HP;
    public int maxHP;
    public int stamina;
    public Weapon weapon;
    public String symbol;
    public ImageIcon icon;
    NPC(int index, int posX,int posY, int maxHP, int stamina, Weapon weapon, String symbol){
        this.index = index;
        this.posX = posX;
        this.posY = posY;
        this.maxHP = maxHP;
        this.HP = this.maxHP;
        this.stamina = stamina;
        this.weapon = weapon;
        this.symbol = symbol;
        this.icon = new ImageIcon("default.png");
    }
}
