package NPCClasses;
import WeaponClasses.*;

public class Soldier extends NPC{
    public Soldier(int index, int posX,int posY, int maxHP, int stamina, Weapon weapon, String symbol){
        super(index, posX, posY, maxHP, stamina, weapon, symbol);
    }
}
