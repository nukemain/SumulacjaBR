public class NPC {
    private int posX;
    private int posY;
    private int HP;
    private int maxHP;
    private int stamina;
    private Weapon weapon;
    NPC(int posX,int posY, int maxHP, int stamina, Weapon weapon){
        this.posX = posX;
        this.posY = posY;
        this.maxHP = maxHP;
        this.HP = this.maxHP;
        this.stamina = stamina;
        this.weapon = weapon;
    }
    //todo:wszystko

}
