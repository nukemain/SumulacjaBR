import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

//Na razie całość trzymam w osobnej klasie i jest zhardcodowana, bo tak mi wygodniej testować
//Wszystkie outputy w konsoli tylko do testów
//wkurza mnie, że za każdym razem jak robię szukam celu to robię nową praktycznie taką samą pętle więc kiedyś to spróbuje poprawić
public class SimulationTest {
    public static void simulation(/*obiekt NPC*/) {
        //Staty NPC do testu:
        int x = 1;
        int y = 1;
        int HP = 49;
        int maxHP = 100;
        int stamina = 1;
        //Staty WPN do testu:
        int DMG = 20;
        int range = 2;
        int quality = 1;
        //Tablice do testu:
        int[][] npc = {{1, 1, HP}, {6, 4, 100}, {1, 7, 54}}; //tymczasowo przechowuje tu też HP (można rozważyć czy nie chcemy tak tego zostawić w finalnej wersji?)
        int[][] wpn = {{5, 9, 2}, {2, 3, 1}, {1, 6, 3}};
        int[][] heal = {{8, 3}, {3, 7}, {10, 10}};
        //Dane celu podróży (to zostaje w finalnej wersji):
        int targetX = -1; //współrzędna x celu
        int targetY = -1; //współrzędna y celu
        double targetDistance = 999; //odległość od celu
        //sprawdzam czy HP mniejsze od 50% i jeśli tak to szuka najbliższej apteczki
        if((double) HP / maxHP < 0.5) {
            //pętla szuka najbliższej apteczki
            //TODO: zabezpieczyć to, żeby nie szukał apteczek jeśli ich nie ma
            for(int i = 0; i < heal.length; i++) {
                double distance = distanceCalc(heal[i][0], heal[i][1], x, y);
                System.out.println(heal[i][0] + " " + heal[i][1] + " " + distance);
                if(distance > 0 && distance < targetDistance) {
                    targetDistance = distance;
                    targetX = heal[i][0];
                    targetY = heal[i][1];
                }
            }
            System.out.println(targetX + " " + targetY + " " + targetDistance);
            //wywołujemy metodę, która zmienia położenie NPC
            movement();
        }
        //jeśli HP większe lub równe 50% to sprawdza czy w zasięgu ataku znajduję się inny NPC
        else {
            boolean inRange = false;
            int targetHP = 999; //ilość punktów życia aktualnego celu
            //pętla sprawdza czy w zasięgu ataku znajdują się inne NPC i jeśli tak to wybiera tego z najmniejszą liczbą HP
            for(int i = 0; i < npc.length; i++) {
                double distance = distanceCalc(npc[i][0], npc[i][1], x, y);
                if(distance > 0 && npc[i][2] < targetHP && distance <= range) {
                    targetDistance = distance;
                    targetHP = npc[i][2];
                    targetX = npc[i][0];
                    targetY = npc[i][1];
                    inRange = true;
                }
            }
            System.out.println(targetX + " " + targetY + " " + targetDistance);
            //jeśli znalazło cel to wywołuje motodę odpowiedzialną za zadawanie obrażeń
            if(inRange == true) {
                damageDealer();
            }
            //jeśli nie to szuka celu podróży (npc lub mocniejszy weapon)
            else {
                //petla szuka najbliższego innego NPC
                for(int i = 0; i < npc.length; i++) {
                    double distance = distanceCalc(npc[i][0], npc[i][1], x, y);
                    System.out.println(npc[i][0] + " " + npc[i][1] + " " + distance);
                    if(distance > 0 && distance < targetDistance) {
                        targetDistance = distance;
                        targetX = npc[i][0];
                        targetY = npc[i][1];
                    }
                }
                //petla szuka najbliższej broni (musi być bliżej niż znaleziony wcześniej NPC)
                System.out.println(targetX + " " + targetY + " " + targetDistance);
                for(int i = 0; i < wpn.length; i++) {
                    if(wpn[i][2] > quality) {
                        double distance = distanceCalc(wpn[i][0], wpn[i][1], x, y);
                        System.out.println(wpn[i][0] + " " + wpn[i][1] + " " + distance);
                        if (distance > 0 && distance < targetDistance) {
                            targetDistance = distance;
                            targetX = wpn[i][0];
                            targetY = wpn[i][1];
                        }
                    }
                }
                System.out.println(targetX + " " + targetY + " " + targetDistance);
                //wywołujemy metodę, która zmienia położnenie NPC
                movement();
            }
        }
    }
    //metoda do obliczania dystansu między dwoma punktami na mapie (prawdopodobnie da się to uprościć, ale wykrzaczało mi się jak próbowałem inaczej, a tak zadziałało więc zostawiłem xd)
    public static double distanceCalc(int targetX, int targetY, int x, int y) {
        double distance = sqrt(abs(x - targetX) * abs(x - targetX) + abs(y - targetY) * abs(y - targetY));
        return  distance;
    }

    public static void movement(){
        //TODO: zmiana pozycji NPC w kierunku celu (koordynaty targetX, targetY)
        // proponuje, żeby NPC mogli się poruszać po skosie, bo wtedy ścieżki, po których się będą poruszać będą bardziej naturalne
        System.out.println("Poruszam się");
    }

    public static void damageDealer(){
        //TODO: zadawanie obrażeń celowi na koordynatach target X, targetY
        System.out.println("Atakuje");
    }
    //main, którego używam tylko do testów i się go potem wyrzuci
    public static void main(String[] args) {
        simulation();
    }
}
