package WeaponClasses;

import javax.swing.*;

import static java.lang.Math.sqrt;

public class Rifle extends Weapon {
    public Rifle(int posX, int posY){
        super(posX, posY);
        this.name = "Rifle";
        this.damage = 10;
        this.range = 2;
        this.quality = 3;
        this.icon = new ImageIcon("rifle.png");
    }
    public int Attack(int targetHP){
        for(int x=0; x<4; x++){
            if((int) (Math.random() * 3) > 1) targetHP -= 5; //30% to do extra 5 dmg
            targetHP -= this.damage;
        }
        return targetHP;
    }
}
