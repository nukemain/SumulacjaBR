import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;
import java.util.Random;
public class Logic {
    public static void main(String[] args) // main() method
    {
        Scanner scanner = new Scanner(System.in);
        System.out.println("menu itp:");
        System.out.println("losowa symulacja-1");
        System.out.println("wyjście-2");
        //int opcja = scanner.nextInt();
        int opcja = 1;
        if(opcja==1){
            System.out.println("symulacja");
            Symulacja(50,50,200); //tymczasowo zhardkodowane
            //TODO: wyłapywanie czy NPCcount nie jest większy od maksymalnej możliwej ilości npc (liczb pól w planszy)
        } else if (opcja==2) {
            System.exit(0);
        }

    }
    static void Symulacja(int sizeX,int sizeY,int NPCcount){
        //TODO: Wczytywanie danych z pliku? - nie trzeba by było przekazywać miliona zmiennych do funkcji

        /*
            sizeX = rozmiar x planszy
            sizeY = rozmiar Y planszy
            NPCcount = ile agentów npc ma mieć symulacja
            WPNcount = ile sztuk broni ma zrespić symulacja

            imo w tej funkcji damy cały kod od symulacji
            póki co najpierw chciałem zacząć od systemu respienia rzeczy, - zrobić taką basic symulację całą i potem rozbudowywac dalej
        */
        String[][]  plansza = new String[sizeX][sizeY];
        Random rand = new Random();

        //poniższa pętla jest po to by móc zapełnić tabelę polami
        //puste (bez NPC, broni, apteczek) pole planszy to "[ ]"
        for(int y=0;y<sizeY;y++){
            for(int x=0;x<sizeX;x++){
                plansza[y][x] = "[ ]";
            }
        }
        /*
        //pętla od respiena NPC - prototyp 1 (gorszy)
        //ten system jest dłuższy, bardziej skomplikowany i przynosi gorsze efekty - zostawiłem go tu tymczasowo tylko dlatego że może mi się przydać jeszcze poźniej

        double chance=0; //% sznasy na zrespienie NPC
        int NPC=0; //licznik ile npc-tów już zostało zrespionych
        int z=0;
        while(NPC<NPCcount) {//jeśli to co jest w pętli nie zdąży zrespić wszystkich npc, to przelatujemy przez tablicę jeszcze raz respiąc pozostałych
            chance=0;
            for (int y = 0; y < sizeY; y++) {
                if (NPC < NPCcount) { //check czy mamy już wystarczajaco NPC
                    for (int x = 0; x < sizeX; x++) {
                        double n = rand.nextDouble();       // losowa z przedziału <0;1>
                        try{
                            if (Objects.equals(plansza[y][x-1], "[x]")||Objects.equals(plansza[y][x+1], "[x]")){
                                plansza[y][x] = "[s]";
                            }
                        } catch (Exception e) {
                            //throw new RuntimeException(e);
                        }
                        if (n < chance) {
                            if (!Objects.equals(plansza[y][x], "[x]")) {
                                plansza[y][x] = "[x]";
                                //tu wywołanie konstruktora klasy NPC
                                chance = 0;
                                NPC++;
                            }
                            if(Objects.equals(plansza[y][x], "[x]")){
                                z++;
                                chance=chance/2;
                            }
                        } else {
                            chance += 0.00025;
                        }
                    }
                }
            }
        }
        System.out.println(z);
        */

        //pętla od respienia NPC - wersja druga
        //TODO: limit npc (prawdopodobnie (sizeX-1 * sizeY-1)-1) - kod za to odpowiedzialny dać przed wywołaniem symulacji !!!

        int NPC=0; //licznik ile npc-tów już zostało zrespionych

        while(NPC<NPCcount){
            int posX= rand.nextInt(0, sizeX);
            int posY= rand.nextInt(0, sizeY);
            try { //try jako zabezpieczenie przed poniższym ifem - żeby program się nie wywalił jak sprawdzi nieistniejący indeks
                if (!Objects.equals(plansza[posY][posX], "[x]") &&
                        !Objects.equals(plansza[posY + 1][posX], "[x]") &&
                        !Objects.equals(plansza[posY + 1][posX + 1], "[x]") &&
                        !Objects.equals(plansza[posY][posX + 1], "[x]") &&
                        !Objects.equals(plansza[posY - 1][posX + 1], "[x]") &&
                        !Objects.equals(plansza[posY - 1][posX], "[x]") &&
                        !Objects.equals(plansza[posY - 1][posX - 1], "[x]") &&
                        !Objects.equals(plansza[posY][posX - 1], "[x]") &&
                        !Objects.equals(plansza[posY + 1][posX - 1], "[x]")) {
                    //powyższy dojebany if sprawdza czy pole w którym respimy NPC
                    //nie jest już zajęte I nie sąsiaduje z innym NPC...

                    plansza[posY][posX] = "[x]";
                    //todo: tu wywołanie konstruktora klasy NPC
                    NPC++;
                }else{
                    //...ale dla urozmaicenia planszy czasami (15% szansy) i tak to zrobi todo:czy 15% to dobra wartość?
                    //w bonusie zabezpiecza to przed przypadkiem gdzie mamy tyle NPC że nie da się ich rozstawic bez sąsiadowania ze sobą
                    if( (rand.nextInt(1,101)<=15) && (!Objects.equals(plansza[posY][posX], "[x]")) ){
                        plansza[posY][posX] = "[x]";
                        //todo: tu wywołanie konstruktora klasy NPC
                        NPC++;
                    }
                }
            } catch(Exception e){/*po prostu zrespić ziutka`gdzieś indziej*/}
        }

        //poniższa pętla liczy ile mamy pól na planszy które nie sąsiadują z żadnym NPC
        //na podstawie ustalonej liczby decydujemu ile zrespić broni i apteczek
        int NoNeighbours=0;
        for (int Y = 0; Y < sizeY; Y++) {
            for (int X = 0; X < sizeX; X++){
                try {
                    if (!Objects.equals(plansza[Y][X], "[x]") &&
                            !Objects.equals(plansza[Y + 1][X], "[x]") &&
                            !Objects.equals(plansza[Y + 1][X + 1], "[x]") &&
                            !Objects.equals(plansza[Y][X + 1], "[x]") &&
                            !Objects.equals(plansza[Y - 1][X + 1], "[x]") &&
                            !Objects.equals(plansza[Y - 1][X], "[x]") &&
                            !Objects.equals(plansza[Y - 1][X - 1], "[x]") &&
                            !Objects.equals(plansza[Y][X - 1], "[x]") &&
                            !Objects.equals(plansza[Y + 1][X - 1], "[x]")) {
                        NoNeighbours++;
                    }
                }catch (Exception e){}
            }
        }
        //nie chcemy by każde możlwie pole to była bróń więc tak o liczbę możliwości dzielę przez 4
        int WPNcount = (NoNeighbours/4);
        int WPN=0;
        while(WPN<WPNcount){
            int posX= rand.nextInt(0, sizeX);
            int posY= rand.nextInt(0, sizeY);
            try { //try jako zabezpieczenie przed poniższym ifem - żeby program się nie wywalił jak sprawdzi nieistniejący indeks
                if (!Objects.equals(plansza[posY][posX], "[x]") && //TODO:ZAMORDOWAĆ TEGO POTWORA
                        !Objects.equals(plansza[posY + 1][posX], "[x]") &&
                        !Objects.equals(plansza[posY + 1][posX + 1], "[x]") &&
                        !Objects.equals(plansza[posY][posX + 1], "[x]") &&
                        !Objects.equals(plansza[posY - 1][posX + 1], "[x]") &&
                        !Objects.equals(plansza[posY - 1][posX], "[x]") &&
                        !Objects.equals(plansza[posY - 1][posX - 1], "[x]") &&
                        !Objects.equals(plansza[posY][posX - 1], "[x]") &&
                        !Objects.equals(plansza[posY + 1][posX - 1], "[x]")&&
                        !Objects.equals(plansza[posY][posX], "[W]") &&
                        !Objects.equals(plansza[posY + 1][posX], "[W]") &&
                        !Objects.equals(plansza[posY + 1][posX + 1], "[W]") &&
                        !Objects.equals(plansza[posY][posX + 1], "[W]") &&
                        !Objects.equals(plansza[posY - 1][posX + 1], "[W]") &&
                        !Objects.equals(plansza[posY - 1][posX], "[W]") &&
                        !Objects.equals(plansza[posY - 1][posX - 1], "[W]") &&
                        !Objects.equals(plansza[posY][posX - 1], "[W]") &&
                        !Objects.equals(plansza[posY + 1][posX - 1], "[W]")) {
                    //powyższy dojebany if sprawdza czy pole w którym respimy NPC
                    //nie jest już zajęte I nie sąsiaduje z innym NPC...

                    plansza[posY][posX] = "[W]";
                    //todo: tu wywołanie konstruktora klasy NPC
                    WPN++;
                }/*else{
                    //...ale dla urozmaicenia planszy czasami (15% szansy) i tak to zrobi todo:czy 15% to dobra wartość?
                    //w bonusie zabezpiecza to przed przypadkiem gdzie mamy tyle NPC że nie da się ich rozstawic bez sąsiadowania ze sobą
                    if( (rand.nextInt(1,101)<=15) && (!Objects.equals(plansza[posY][posX], "[x]")) ){
                        plansza[posY][posX] = "[x]";
                        //todo: tu wywołanie konstruktora klasy NPC
                        NPC++;
                    }
                }*/
            } catch(Exception e){/*po prostu zrespić ziutka`gdzieś indziej*/}
        }


        //pętla od drukowania planszy
        for(int y=0;y<sizeY;y++){
            for(int x=0;x<sizeX;x++){
                System.out.print(plansza[y][x]);
            }
            System.out.println();
        }
        System.out.println(NoNeighbours);
    }
}
