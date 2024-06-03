package WeaponClasses;

import static java.lang.Math.sqrt;

public class Knife extends Weapon {
    public Knife(int posX, int posY){
        super(posX, posY);
        this.name = "Knife";
        this.damage = 15;
        this.range = sqrt(2);
        this.quality = 0;
    }
    public int Attack(int targetHP){
        if((int) (Math.random() * 100) > 94) targetHP = -1; //5% to one-shot enemy
        else targetHP -= this.damage;
        return targetHP;
    }
}
