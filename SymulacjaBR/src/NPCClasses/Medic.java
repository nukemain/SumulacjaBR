package NPCClasses;
import WeaponClasses.*;
import javax.swing.*;

public class Medic extends NPC{
    String[] names = {"Worminator", "Wriggler", "Earthworm Jim", "Slimy", "Creeper", "Squirm", "Gummy", "Wiggly", "Slither", "Maggot"};
    public Medic(int index, int posX,int posY,Weapon weapon){
        super(index, posX, posY, weapon);
        this.maxHP = 100;
        this.HP = this.maxHP;
        this.stamina = 2;
        this.symbol = "M";
        this.name = names[(int) (Math.random() * (10))];
        this.icon = new ImageIcon(getClass().getResource("/medic.png"));
    }
    public void Ability(){
        //heal 4 HP on turn
        if(this.HP <= 70){
            this.HP += 4;
        }
    }
}
