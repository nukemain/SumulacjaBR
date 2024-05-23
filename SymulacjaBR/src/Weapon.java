import static java.lang.Math.sqrt;

public class Weapon {
    String name;
    int damage;
    double range;
    int quality;
    int posX;
    int posY;
    Weapon(String name, int damage, int range, int quality, int posX, int posY) {
        this.name = name;
        this.damage = damage;
        this.range = range*sqrt(2);
        this.quality = quality;
        this.posX = posX;
        this.posY = posY;
    }
}
