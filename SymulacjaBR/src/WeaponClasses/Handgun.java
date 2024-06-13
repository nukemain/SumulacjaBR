package WeaponClasses;
import javax.swing.*;
import static java.lang.Math.sqrt;

public class Handgun extends Weapon {
    public Handgun(int posX, int posY){
        super(posX, posY);
        this.name = "Handgun";
        this.damage = 25;
        this.range = sqrt(2);
        this.quality = 1;
        this.icon = new ImageIcon(getClass().getResource("/handgun.png"));
    }
    public int Attack(int targetHP){
        targetHP -= this.damage;
        return targetHP;
    }
}
