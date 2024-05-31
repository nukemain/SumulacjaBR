import java.io.FileNotFoundException;
import java.util.Scanner;

public class Controller {
    public static void main(String[] args) throws FileNotFoundException {
        //TODO: Na koniec jak już będzie śmigać dodać input od użytkownika w GUI.
        int NPCcount = 10;
        int size=30;
        int sizeX=30;
        int sizeY=sizeX; //plansza ma być kwadratem, bo tak łatwiej w życiu i tyle pozdro

        Scanner menu = new Scanner(System.in);
        /* wykomentowałem bo robiąc gui mi to przeszkadzało
        System.out.println("Menu:");
        System.out.println("1. Use current settings");
        System.out.println("2. Load settings from the file");
        int loadingMenu = menu.nextInt();*/
        int loadingMenu =1;
        if(loadingMenu == 1) {
            NPCcount = 10;
            sizeX = 30; //todo: 50 to maksymalny rozmiar (dla większych nadal działa, ale wygląda po prostu źle)
            sizeY = sizeX;
        }
        else if(loadingMenu == 2) {
            FileReader.fileReader();
            NPCcount = FileReader.npcCount;
            sizeX = FileReader.size;
            sizeY = sizeX;
        }
        else {
            System.out.println("Choose 1 or 2");
        }

        if(NPCcount>(sizeX-1)*(sizeY-1)*0.25){ //mniej niż 25% planszy to npc -> czemu 25%? liczba wybrana tak o z dupy + cała plansza npctów srednio działa
            System.out.println("Number of NPCs can not be higher than amount of fields.");
        }
        else{
            Logic.Symulacja(size,NPCcount);
        }
    }
}
//WIELKA LISTA RZECZY DO ZROBIENIA
//TĄ SEKCJĘ MUSIMY ZROBIĆ
//TODO: TOP 1 PRIO !!! -> ŻEBY KLASY COŚ ROBIŁY,nwm co cokolwiek ale obecnie jedyne czym się różnią klasy to samymi statami, a ma być coś więcej. co? nie wiem - spotkajmy przedyskutujmy
//TODO:teren -> zrobiony szumem perlina (pytać Piotra), myślę że tak z trzy rózne rodzaje terenu conajmniej, pełna współpraca z AI npc
//todo:wczytywanie plików
//todo:zapisywanie do plików
//todo:główne okienko symulacji (obecne jest secondary) - zostawcie to mi
//todo:zmniejszanie sie strefy
//imo jak rzeczy wyżej będą gotowe to mozna mówić o gotowym projekcie


//rzeczy niżej nie są konieczne do zrobienia ale fajnie by było jak by były
//todo: ładniejsze GUI - ja sie tym zajmę
//todo: airdropy? -> wymagało by to jakiejś bonusowej nowej broni (minigun? rpg? obydwa?)


