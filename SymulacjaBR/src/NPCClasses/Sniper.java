package NPCClasses;
import WeaponClasses.*;

import javax.swing.*;
import java.util.Objects;

public class Sniper extends NPC{
    String[] names = {"Wormsworth", "Gnasher", "Inchworm", "Chomper", "Squishy", "Crawler", "Hookworm", "Vermie", "Squiggler", "Burrower"};
    String boostedWeapon = "NULL";
    public Sniper(int index, int posX,int posY,Weapon weapon){
        super(index, posX, posY, weapon);
        this.maxHP = 100;
        this.HP = this.maxHP;
        this.stamina = 2;
        this.symbol = "Θ";
        this.name = names[(int) (Math.random() * (10))];
        this.icon = new ImageIcon("images/sniper.png");
    }
    public void Ability(){
        if(!Objects.equals(this.weapon.name, "Knife") && !Objects.equals(this.weapon.name, this.boostedWeapon)) this.weapon.range += 1;
        this.boostedWeapon = this.weapon.name;
    }
}
