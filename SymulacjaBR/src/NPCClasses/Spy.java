package NPCClasses;
import WeaponClasses.*;

import javax.swing.*;

public class Spy extends NPC{
    String[] names = {"Twine", "Coil", "Wringer", "Snappy", "Bumpy", "Curly", "Pinky", "Hoppy", "Jumpy", "Slippy"};
    public Spy(int index, int posX,int posY,Weapon weapon){
        super(index, posX, posY, weapon);
        this.maxHP = 80;
        this.HP = this.maxHP;
        this.stamina = 2;
        this.symbol = "Ω";
        this.name = names[(int) (Math.random() * (10))];
        this.icon = new ImageIcon("spy.png");
    }
    //Has 30% chance to evade hit
}
