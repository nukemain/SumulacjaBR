public class NPC {
    public int index;
    public int posX;
    public int posY;
    public int HP;
    public int maxHP;
    public int stamina;
    public Weapon weapon;
    NPC(int index, int posX,int posY, int maxHP, int stamina, Weapon weapon){
        this.index = index;
        this.posX = posX;
        this.posY = posY;
        this.maxHP = maxHP;
        this.HP = this.maxHP;
        this.stamina = stamina;
        this.weapon = weapon;
    }
    //todo:wszystko

}
