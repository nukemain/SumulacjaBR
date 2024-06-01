import java.io.FileNotFoundException;
import java.util.Scanner;

public class Controller {
    static int size =0;
    public static void main(String[] args) throws FileNotFoundException {
        //TODO: Na koniec jak już będzie śmigać dodać input od użytkownika w GUI.
        int NPCcount = 10;
        size=30;

        //Scanner menu = new Scanner(System.in);

        Logic.Symulacja(size,NPCcount);//todo: kontrola ilości npc
    }
}
//WIELKA LISTA RZECZY DO ZROBIENIA
//TĄ SEKCJĘ MUSIMY ZROBIĆ
//TODO: TOP 1 PRIO !!! -> ŻEBY KLASY COŚ ROBIŁY,nwm co cokolwiek ale obecnie jedyne czym się różnią klasy to samymi statami, a ma być coś więcej. co? nie wiem - spotkajmy przedyskutujmy
//TODO:teren -> zrobiony szumem perlina (pytać Piotra), myślę że tak z trzy rózne rodzaje terenu conajmniej, pełna współpraca z AI npc
//done:wczytywanie plików
//done:zapisywanie do plików
//todo:główne okienko symulacji (obecne jest secondary) - zostawcie to mi
//todo:zmniejszanie sie strefy
//done :gui do innej klasy
//imo jak rzeczy wyżej będą gotowe to mozna mówić o gotowym projekcie


//rzeczy niżej nie są konieczne do zrobienia ale fajnie by było jak by były
//todo: ładniejsze GUI - ja sie tym zajmę
//todo: airdropy? -> wymagało by to jakiejś bonusowej nowej broni (minigun? rpg? obydwa?)
//todo: mechanika resizu okna (program działa bez, ale lekko ucina)


