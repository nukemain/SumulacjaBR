import javax.swing.*;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Controller {
    static int size = 1;
    static int NPCcount = 10;
    static JFrame SimulationFrame = new JFrame();

    public static void main(String[] args){
        //GUI.MenuGUI(MenuFrame);

        //NPCcount = 10;
        //size=40; //10 min 50 maks (dla większych liczb działą ale strasznie się zacina i ikonki są mega małe)
        //Scanner menu = new Scanner(System.in);
        Logic.Symulacja();//todo: kontrola ilości npc
    }
}
//WIELKA LISTA RZECZY DO ZROBIENIA
//TĄ SEKCJĘ MUSIMY ZROBIĆ]
//TODO: https://discord.com/channels/1236752666273775667/1236752666781290557/1246940156083310732
//TODO: teren -> zrobiony szumem perlina (pytać Piotra), myślę że tak z trzy rózne rodzaje terenu conajmniej, pełna współpraca z AI npc
//done: wczytywanie plików
//done: zapisywanie do plików
//todo: input od użytkownika do gui
//todo: zmniejszanie sie strefy - to do zrobienia po terenie - strefa jako rodzaj terenu który bije dmg temu co na tym stoi
//done: gui do innej klasy
//imo jak rzeczy wyżej będą gotowe to mozna mówić o gotowym projekcie


//rzeczy niżej nie są konieczne do zrobienia ale fajnie by było jak by były
//todo: rewrite spawningu (większa kontrola nad ilością respionej broni i medkitów - zamiast "losowa" liczba jak jest teraz)
//todo: airdropy? -> wymagało by to jakiejś bonusowej nowej broni (minigun? rpg? obydwa?)
//todo: mechanika resizu okna (program działa bez, ale lekko ucina)


