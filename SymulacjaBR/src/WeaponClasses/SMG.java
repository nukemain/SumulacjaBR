package WeaponClasses;
import javax.swing.*;
import static java.lang.Math.sqrt;

public class SMG extends Weapon {
    public SMG(int posX, int posY){
        super(posX, posY);
        this.name = "SMG";
        this.damage = 35;
        this.range = sqrt(2);
        this.quality = 3;
        this.icon = new ImageIcon(getClass().getResource("/smg.png"));
    }
}
