package NPCClasses;
import WeaponClasses.*;
import javax.swing.ImageIcon;
import java.util.Objects;

public class Soldier extends NPC{
    String[] names = {"Slick", "Twister", "Fuzzy", "Gusher", "Slinky", "Scooter", "Looper", "Bender", "Flex", "Slinky"};
    public Soldier(int index, int posX,int posY,Weapon weapon){
        super(index, posX, posY, weapon);
        this.maxHP = 140;
        this.HP = this.maxHP;
        this.stamina = 2;
        this.symbol = "Σ";
        this.name = names[(int) (Math.random() * (10))];
        this.icon = new ImageIcon("images/zolnierz.png");
    }
    //If target is in '1' range hit for 120%
}
