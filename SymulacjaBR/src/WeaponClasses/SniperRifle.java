package WeaponClasses;
import javax.swing.*;
import static java.lang.Math.sqrt;

public class SniperRifle extends Weapon {
    public SniperRifle(int posX, int posY){
        super(posX, posY);
        this.name = "SniperRifle";
        this.damage = 40;
        this.range = 3;
        this.quality = 4;
        this.icon = new ImageIcon("images/sniperrifle.png");
    }
    public int Attack(int targetHP){
        if((int) (Math.random() * 3) > 1) targetHP -= this.damage*0.5; //50% to crit (headshot) for 150% dmg
        targetHP -= this.damage;
        return targetHP;
    }
}
