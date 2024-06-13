package WeaponClasses;
import javax.swing.*;
import java.awt.*;
import java.io.InputStream;

import static java.lang.Math.sqrt;

public class Shotgun extends Weapon {
    public Shotgun(int posX, int posY){
        super(posX, posY);
        this.name = "Shotgun";
        this.damage = 10;
        this.range = sqrt(2);
        this.quality = 2;
        //this.icon = new ImageIcon("images/shotgun.png");
        this.icon = new ImageIcon(getClass().getResource("/shotgun.png"));

    }
    public int Attack(int targetHP){
        for(int x=0; x<8; x++){
            if((int) (Math.random() * 2) > 0) targetHP -= this.damage; //50% to hit one of 8 bullets
        }
        return targetHP;
    }
}
